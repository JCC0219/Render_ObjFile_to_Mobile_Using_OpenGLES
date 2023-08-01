package com.example.assignment;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class OpenGLView extends GLSurfaceView {
    public OpenGLView(Context context) {
        super(context);
        init();
    }

    public OpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // to initialize the other features
    private void init() {
        // set OpenGL ES 2.0
        setEGLContextClientVersion(2);
        // store OpenGL context
        setPreserveEGLContextOnPause(true);
        // set the Renderer for drawing on the GLSurfaceView
        setRenderer(new OpenGLRenderer(getContext()));
    }
}
