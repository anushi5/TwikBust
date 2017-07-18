package com.example.anushi.twikbust;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        Bundle b=getIntent().getExtras();
        String user_name=b.getString("user_name");
        String dp_picture_path=b.getString("dp_picture_path");
    }
}
