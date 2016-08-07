package com.sparshik.yogicapple.ui.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sparshik.yogicapple.R;

import java.io.File;

public class MediaPlayerActivity extends AppCompatActivity implements OnActionClickedListener {
    private static final String LOG_TAG = MediaPlayerActivity.class.getSimpleName();
    //handles playback of all sound files
    private MediaPlayer mPlayer;
    private AudioManager mAudio;
    private InteractivePlayerView ipv;
    private ImageView control;

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
                mPlayer.pause();
                mPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                stopPlaying();
            }
        }
    };

    /**
     * This listener get triggered when the {@link MediaPlayer} has completed
     * playing the audio file
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mPlayer) {
            stopPlaying();
            ipv.stop();
            control.setBackgroundResource(R.drawable.ic_play_dark);
            ipv.setProgress(0);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        mAudio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        ipv = (InteractivePlayerView) findViewById(R.id.ipv);
        ipv.setProgress(0);
        ipv.setMax(123);
        ipv.setOnActionClickedListener(this);
        stopPlaying();
        //Request audio focus for playback
        int result = mAudio.requestAudioFocus(
                afChangeListener, //change listner
                AudioManager.STREAM_MUSIC,//Use the music Stream
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT //Request transient focus
        );

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            File path = new File(getApplicationContext().getFilesDir(), "misc");
            path.mkdirs();
            File localFile = new File(path, "ankita.mp3");

            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(localFile.toString());
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            try {
                mPlayer.prepare();
            } catch (IllegalStateException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            Log.d(LOG_TAG, mPlayer.getDuration() + "Testing");
            int duration = mPlayer.getDuration() / 1000;
            if (duration != 0) {
                ipv.setMax(duration);
            }
        }
        control = (ImageView) findViewById(R.id.control);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ipv.isPlaying()) {
                    ipv.start();
                    mPlayer.start();
                    mPlayer.setOnCompletionListener(mCompletionListener);
                    control.setBackgroundResource(R.drawable.ic_pause_dark);
                } else {
                    ipv.stop();
                    control.setBackgroundResource(R.drawable.ic_play_dark);
                    mPlayer.pause();
                }
            }
        });
    }

    @Override
    public void onActionClicked(int id) {

        switch (id) {
            case 1:
                //Called when 1. action is clicked.
                Log.d(LOG_TAG, "Action 1 tried");
                break;
            case 2:
                //Called when 2. action is clicked.
                Log.d(LOG_TAG, "Action 2 tried");
                break;
            case 3:
                //Called when 3. action is clicked.
                Log.d(LOG_TAG, "Action 3 tried");
                break;
            default:
                break;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlaying();
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mAudio.abandonAudioFocus(afChangeListener);

        }
    }

}
