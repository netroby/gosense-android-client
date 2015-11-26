package com.netroby.app.android.gosense.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.netroby.app.android.gosense.client.R;
import com.netroby.app.android.gosense.client.model.BlogList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ViewBlogActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        Intent intent = getIntent();
        String aid = intent.getStringExtra(MainActivity.BLOG_AID);

        new DownloadBlogTask().execute("https://www.netroby.com/api/view/" + aid);
    }

    private class DownloadBlogTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: " + e);
                return new JSONObject();
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            Log.d(TAG, "Result" + result.toString());
            Integer i = 0;
            TextView tv = (TextView) findViewById(R.id.blogViewContainer);
            try {
                tv.setText(result.getString("content"));
                TextView title = (TextView) findViewById(R.id.blogTitleView);
                title.setText(result.getString("title"));
                tv.setMovementMethod(new ScrollingMovementMethod());
            } catch (Exception e) {
                Log.d(TAG, "Error " + e.toString());
            }
        }
        private JSONObject downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String respString = readIt(is);

                return new JSONObject(respString);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                Log.d(TAG, "downloadUrl: " + e);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return new JSONObject();
        }
        public String readIt(InputStream stream) throws IOException {

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

}
