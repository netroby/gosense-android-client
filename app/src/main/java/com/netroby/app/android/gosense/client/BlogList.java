package com.netroby.app.android.gosense.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BlogList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);


        List<String> data = new ArrayList<>();
        data.add("1. 星期天");
        data.add("2. 早上大家很忙");
        data.add("3. 没有什么不同");
        data.add("4. 看不见就算了");


        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, data));
    }
}
