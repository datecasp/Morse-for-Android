package com.example.morseforandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity
{
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
    }

    public void btnSendClick (View v)
    {
        Intent intent = new Intent(this, SendMessageActivity.class);
        startActivity(intent);
    }
}