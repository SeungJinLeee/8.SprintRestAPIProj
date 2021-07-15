package com.kosmo.fcmmessagingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MoveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_layout);
        Intent intent = getIntent();
        String title = intent.getStringExtra("dataTitle");
        String body = intent.getStringExtra("dataBody");
        ((TextView)findViewById(R.id.textTitle)).setText(title);
        ((TextView)findViewById(R.id.textBody)).setText(body);
    }
}
