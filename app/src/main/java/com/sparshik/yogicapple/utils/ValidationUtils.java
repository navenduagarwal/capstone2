package com.sparshik.yogicapple.utils;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import com.sparshik.yogicapple.R;

/**
 * utility to validate strings or values
 */
public class ValidationUtils {


    public static boolean isUrlValid(Context context, EditText editText, String url) {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            editText.setError(context.getString(R.string.error_invalid_url));
            return false;
        }
        return true;
    }


//
//    public static class isUrlValid extends AsyncTask<String, Void, Integer> {
//        private final String LOG_TAG = isUrlValid.class.getSimpleName();
//        private final WeakReference<EditText> editTextWeakReference;
//
//        public isUrlValid(EditText editText){
//            editTextWeakReference = new WeakReference<EditText>(editText);
//        }
//
//        @Override
//        protected Integer doInBackground(String... strings) {
//            HttpURLConnection httpURLConnection = null;
//            try {
//                httpURLConnection = (HttpURLConnection) new URL(strings[0]).openConnection();
//                httpURLConnection.setConnectTimeout(Constants.URL_TIMEOUT);
//                httpURLConnection.setReadTimeout(Constants.URL_TIMEOUT);
//                httpURLConnection.setRequestMethod("HEAD");
//                return httpURLConnection.getResponseCode();
//            }catch (IOException exception) {
//                Log.e(LOG_TAG, exception.getMessage());
//                return 0;
//            } finally {
//                if (httpURLConnection !=null){
//                    httpURLConnection.disconnect();
//                }
//            }
//        }
//
//        @Override
//        protected boolean onPostExecute(Integer integer) {
//            if(integer !=200){
//                editText.setError(context.getString(R.string.error_invalid_url));
//                return false;
//            }
//            return true;
//        }
//    }
}
