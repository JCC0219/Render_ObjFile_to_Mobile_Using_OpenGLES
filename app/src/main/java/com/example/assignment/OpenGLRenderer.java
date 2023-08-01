package com.example.assignment;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.renderscript.Float3;
import android.renderscript.Matrix4f;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.javagl.obj.Obj;

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    private Context context;
    private ObjLoad obj_loader;

    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //this obj file contain vec3 vertices, vec3 normal, vec2 texture, vec4 face
        try {
            //read obj file
            obj_loader = new ObjLoad(context, "virus/virus.obj");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        obj_loader.setPosition(new Float3(0.0f,0.0f,0.0f));

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float aspectRatio = (float)width/(float)height;
        GLES20.glViewport(0, 0, width, height);

        if(obj_loader != null){
            Matrix4f perspective = new Matrix4f();
            perspective.loadPerspective(85.0f,aspectRatio,1.0f,150.0f);
            obj_loader.setProjection(perspective);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //background color
        GLES20.glClearColor(1.0f, 0.5f, 0.2f, 1.0f);
        // clear the front buffer to produce back buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //virus.draw();

        updateWithDelta(1);// 1 mean nothing here, might change after, just used to call the function below
        //obj_loader.draw();
    }

    public void updateWithDelta(long dt){



        Matrix4f camera = new Matrix4f();

        camera.translate(0.0f,0.0f,-2.0f);
       /* //camera.rotate(0.0f,0.0f,0.0f,1.0f);
        camera.rotate(0.0f,1.0f,0.0f,0.0f);
        camera.rotate(0.0f,0.0f,1.0f,0.0f);
        camera.rotate(0.0f,0.0f,0.0f,1.0f);
        camera.scale(1.0f,1.0f,1.0f);*/

        obj_loader.setCamera(camera);

        obj_loader.draw(dt);
    }


}
