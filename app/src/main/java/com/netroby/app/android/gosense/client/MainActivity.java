package com.netroby.app.android.gosense.client;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    public static final String BLOG_AID = "com.netroby.app.android.gosense.client.MainActivity.AID";
    //Define home index page for main activity
    public static final String INDEX_PAGE = "com.netroby.app.android.gosense.client.MainActivity.INDEXPAGE";
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String INDEXPAGE = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Integer.parseInt(MainActivity.INDEXPAGE) <= 0) {
            MainActivity.INDEXPAGE = "1";
        }
        new DownloadWebpageTask().execute("https://www.netroby.com/api?page=" + MainActivity.INDEXPAGE);

        Button prevBtn = (Button) findViewById(R.id.previousPage);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Yes, i Clicked", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.BLOG_AID, Integer.toString(2));
                startActivity(intent);
            }
        });

        Button nextBtn = (Button) findViewById(R.id.nextPage);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Yes, i Clicked", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.BLOG_AID, Integer.toString(2));
                startActivity(intent);
            }
        });
        /**
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setText("Hello world, I am glad to see you here");
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("Begin loading files");


         **/
    }
    private class BlogListAdapter extends ArrayAdapter<BlogList> {
        public BlogListAdapter(Context context, ArrayList<BlogList> bloglists) {
            super(context, 0, bloglists);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BlogList b = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_blog_list, parent, false);
            }
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            tvTitle.setText(b.title);
            return convertView;
        }
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: " + e);
                return new JSONArray();
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONArray result) {
            Log.d(TAG, "Result" + result.toString());
            final ArrayList<BlogList> blogLists = new ArrayList<>();
            Integer i = 0;
            for (i = 0; i < result.length(); i++) {
                try {
                    JSONObject blog = result.getJSONObject(i);
                    blogLists.add(new BlogList(blog.getInt("aid"), blog.getString("title")));
                } catch (Exception e) {
                    Log.d(TAG, "result get: " + e);
                }
            }
            blogLists.add(new BlogList(133, "This is a test"));

            ListView lv = (ListView) findViewById(R.id.listView1);
            lv.setAdapter(new BlogListAdapter(MainActivity.this, blogLists));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BlogList b = (BlogList) parent.getItemAtPosition(position);
                    Log.d(TAG, "item pressed " + b.aid);
                    Intent intent = new Intent(MainActivity.this, ViewBlogActivity.class);
                    intent.putExtra(MainActivity.BLOG_AID, Integer.toString(b.aid));
                    startActivity(intent);

                }
            });
        }
        private JSONArray downloadUrl(String myurl) throws IOException {
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

                return new JSONArray(respString);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                Log.d(TAG, "downloadUrl: " + e);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return new JSONArray();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
