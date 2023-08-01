package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.assignment.OpenGLView;

public class MainActivity extends AppCompatActivity {
    // the view private
    OpenGLView myOpenGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call the view layout --> renderer --> draw
        myOpenGLView = (OpenGLView) findViewById(R.id.myopenglview);
    }

    protected void onPause() {
        super.onPause();
        myOpenGLView.onPause();
    }

    protected void onResume() {
        super.onResume();
        myOpenGLView.onResume();
    }
}