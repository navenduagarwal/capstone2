package com.sparshik.yogicapple.ui.player;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Pack;
import com.sparshik.yogicapple.model.PackApple;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.ui.main.MainActivity;
import com.sparshik.yogicapple.utils.CommonUtils;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.views.InteractivePlayerView;

import java.io.File;

public class ExoPlayerActivity extends BaseActivity implements ExoPlayer.Listener {
    private static final String LOG_TAG = ExoPlayerActivity.class.getSimpleName();
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final int RENDERER_COUNT = 1;
    private static String mAudioUrl, mVideoUrl;
    private String mPackId, mAppleId, mProgramId;
    private Uri mAudioUri;
    private AudioManager mAudio;
    private InteractivePlayerView ipv;
    private ImageView control, close;
    private DatabaseReference mAppleStatusRef, mPackRef, mAppleRef;
    private TextView TextViewAppleGuideName, TextViewAppleText, TextViewDurationText, TextViewPackTitle;
    private int numRenderers = 1; // since only audio, if video too make it 2
    private ExoPlayer player;
    private TrackRenderer mAudioRenderer;
    private String playerState;
    private Handler mainHandler;
    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                player.stop();
                player.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                startPlayBack();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                stopPlayback();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //Updating view header info and getting audioURl
        final Intent intent = getIntent();
        mPackId = intent.getStringExtra(Constants.KEY_PACK_ID);
        mAppleId = intent.getStringExtra(Constants.KEY_APPLE_ID);
        mProgramId = intent.getStringExtra(Constants.KEY_PROGRAM_ID);
        mAudioUrl = intent.getStringExtra(Constants.KEY_AUDIO_URL);

        initializeView();

        mainHandler = new Handler();


        mPackRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_PROGRAM_PACKS).child(mProgramId).child(mPackId);

        mAppleRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_PACK_APPLES).child(mPackId).child(mAppleId);

        mAppleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final PackApple packApple = dataSnapshot.getValue(PackApple.class);
                if (packApple != null) {
                    TextViewAppleText.setText(packApple.getAppleTitle());
                    String duration = getString(R.string.format_duration, packApple.getAppleDuration() / 60);
                    TextViewDurationText.setText(duration);
                    if (packApple.getAppleGuideName() != null) {
                        String guide = getString(R.string.format_guide, packApple.getAppleGuideName());
                        SpannableString underline = new SpannableString(guide);
                        underline.setSpan(new UnderlineSpan(), 0, underline.length(), 0);
                        TextViewAppleGuideName.setText(underline);
                    } else {
                        TextViewAppleGuideName.setVisibility(View.GONE);
                    }

                    if (packApple.getAppleGuideSiteUrl() != null) {
                        TextViewAppleGuideName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonUtils.openWebsite(ExoPlayerActivity.this, packApple.getAppleGuideSiteUrl());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mPackRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pack pack = dataSnapshot.getValue(Pack.class);
                if (pack != null) {
                    String packTitleName = getString(R.string.format_pack_apple_title, pack.getPackTitle());
                    TextViewPackTitle.setText(packTitleName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed));
            }
        });

        mAudio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);


        //Converting local filename string to Uri

        mAudioUri = Uri.parse(mAudioUrl);

        //Initializing Player UI
        ipv = (InteractivePlayerView) findViewById(R.id.ipv);
        ipv.setProgress(0);

        //Converting local filename string to Uri
        mAudioUri = Uri.parse(mAudioUrl);

        //Finding out media duration
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        if (mAudioUri != null) {
            mediaMetadataRetriever.setDataSource(this, mAudioUri);
        }
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int durationSecond = Integer.parseInt(durationStr) / 1000;
        ipv.setMax(durationSecond);

        stopPlayback();
        startPlayBack();
        File file = new File(mAudioUrl);
        if (!file.exists()) {
            Toast.makeText(this, getString(R.string.file_unavailable), Toast.LENGTH_SHORT).show();
            intentApplesList();
        }


        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ipv.isPlaying()) {
                    if (!maybeRequestPermission()) {
                        if (player == null) {
                            startPlayBack();
                        }
                        player.setPlayWhenReady(true);
                        ipv.start();
                        control.setBackgroundResource(R.drawable.ic_media_pause);
                    }
                } else {
                    ipv.stop();
                    control.setBackgroundResource(R.drawable.ic_media_play);
                    player.setPlayWhenReady(false);
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlayback();
                intentApplesList();
            }
        });

    }

    public void startPlayBack() {

        int result = mAudio.requestAudioFocus(
                afChangeListener, //change listner
                AudioManager.STREAM_MUSIC,//Use the music Stream
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT //Request transient focus
        );

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Setting up player
            player = ExoPlayer.Factory.newInstance(RENDERER_COUNT);
            player.addListener(this);
            Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
            String userAgent = Util.getUserAgent(this, "ExoPlayerAudio");

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mainHandler, null);
            DataSource datasource = new DefaultUriDataSource(this, bandwidthMeter, userAgent);

            //Build the same source
            ExtractorSampleSource sampleSource =
                    new ExtractorSampleSource(mAudioUri, datasource, allocator,
                            BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

            mAudioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                    MediaCodecSelector.DEFAULT);
            player.prepare(mAudioRenderer);
        }
    }


    // Permission management methods

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startPlayBack();
        } else {
            Toast.makeText(getApplicationContext(), R.string.storage_permission_denied,
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Checks whether it is necessary to ask for permission to read storage. If necessary, it also
     * requests permission.
     *
     * @return true if a permission request is made. False if it is not necessary.
     */
    @TargetApi(23)
    private boolean maybeRequestPermission() {
        if (requiresPermission(mAudioUri)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(23)
    private boolean requiresPermission(Uri uri) {
        return Util.SDK_INT >= 23 && Util.isLocalFileUri(uri)
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        savePlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayback();
    }

    private void savePlayback() {
        ipv.stop();
        if (player != null) {
            control.setBackgroundResource(R.drawable.ic_media_play);
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void stopPlayback() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            mAudio.abandonAudioFocus(afChangeListener);
            ipv.stop();
            control.setBackgroundResource(R.drawable.ic_media_play);
            ipv.setProgress(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current music state
        savedInstanceState.putInt(Constants.KEY_PLAYER_POSITION, ipv.getProgress());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        int savedProgress = savedInstanceState.getInt(Constants.KEY_PLAYER_POSITION);
        ipv.setProgress(savedProgress);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (player.getPlaybackState()) {
            case ExoPlayer.STATE_BUFFERING:
                playerState = "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                playerState = "ended";
                stopPlayback();
                Log.d("Testing Player", "ended");
                break;
            case ExoPlayer.STATE_IDLE:
                playerState = "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                playerState = "preparing";
                break;
            case ExoPlayer.STATE_READY:
                playerState = "ready";
                break;
        }
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    private void initializeView() {
        control = (ImageView) findViewById(R.id.control);
        close = (ImageView) findViewById(R.id.action_close);
        TextViewPackTitle = (TextView) findViewById(R.id.text_view_pack_title);
        TextViewAppleText = (TextView) findViewById(R.id.text_view_apples);
        TextViewDurationText = (TextView) findViewById(R.id.durationText);
        TextViewAppleGuideName = (TextView) findViewById(R.id.text_view_apple_guide);
    }


    public void intentApplesList() {
        Intent intent = new Intent(ExoPlayerActivity.this, MainActivity.class);
//        intent.putExtra(Constants.KEY_PROGRAM_ID, mProgramId);
//        intent.putExtra(Constants.KEY_PACK_ID, mPackId);
//                    /* Start an activity showing the packs for selected program */
        startActivity(intent);
        finish();
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        stopPlayback();
        player.seekTo(0);
        setIntent(intent);
    }
}
