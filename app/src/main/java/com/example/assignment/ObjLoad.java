package com.example.assignment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.opengl.GLES20;
import android.renderscript.Float3;
import android.renderscript.Matrix4f;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import glkit.BufferUtils;
import glkit.ShaderProgram;
import glkit.ShaderUtils;

public class ObjLoad {
    //shader program
    public ShaderProgram shader;

    //Array for storing data from obj
    //indices array
    protected int[] vertex_indices;

    protected int[] normal_indices;
    protected int[] teex_indices;
    //data array
    protected float[] vertices;
    protected float[] normals;
    protected float[] teexCoords;

    //Configure for VBO
    private FloatBuffer vertexBuffer;
    private int vertexBufferID;

    //Configure for EBO
    private ByteBuffer indexBuffer;
    private int indexBufferId;

    private int vertexStride;
    private int vertexCount;

    //constant value
    static final int COORDS_PER_VERTEX = 3; // 3 data represent 1 vertex



    // ModelView Transformation
    private Float3 position = new Float3(0.0f,0.0f,0f);
    private float rotationX = 0.0f;
    private float rotationY = 0.0f;
    private float rotationZ = 0.0f;
    private float scale = 1.0f;

    public void setPosition(Float3 position) {
        this.position = position;
    }

    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }

    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }


    // Camera view
    protected Matrix4f camera = new Matrix4f();

    // Projection
    protected Matrix4f projection = new Matrix4f();

    public ObjLoad(Context context, String filepath) throws IOException {
        InputStream inputStream = context.getAssets().open(filepath);
        Obj obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));

        // Get the mesh data
        //get (vertex, normal, texture) indices array
        vertex_indices = ObjData.getFaceVertexIndicesArray(obj, 3);
        normal_indices = ObjData.getFaceNormalIndicesArray(obj,3);
        teex_indices = ObjData.getFaceTexCoordIndicesArray(obj,3);

        //save data in float array
        //v(x,y,z)
        vertices = ObjData.getVerticesArray(obj); // create vertex buffer
        //vn(x,y,z)
        normals = ObjData.getNormalsArray(obj);
        //vt(u,v)
        teexCoords = ObjData.getTexCoordsArray(obj,2);



        //print for testing usage (can delete after use)
//        System.out.println(Arrays.toString(vertex_indices));
//
//        System.out.println(vertices[42350*3]);
//        System.out.println(vertices[42351*3]);
//
//        System.out.println(vertices.length);



        setupShader(context);
        setupVertexBuffer();
        setupIndexBuffer();
    }

    private void setupShader(Context context) {
        shader = new ShaderProgram(
                ShaderUtils.readShaderFileFromRawResource(context,
                        R.raw.simple_vertex_shader),
                ShaderUtils.readShaderFileFromRawResource(context,
                        R.raw.simple_fragment_shader)
        );
        vertexStride = COORDS_PER_VERTEX * 4; //4 byte per vertex
        vertexCount = vertices.length / COORDS_PER_VERTEX;//how many set of vertices


    }

    //
    private void setupIndexBuffer() {
        // Generate OpenGL format array for sending to GPU
        indexBuffer = ByteBuffer.allocateDirect(vertex_indices.length * 4);
        indexBuffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < vertex_indices.length; i++) {
            indexBuffer.putInt(vertex_indices[i]);
        }
        indexBuffer.position(0);

        // Generate a buffer object in GPU
        int[] buffer = new int[1];
        GLES20.glGenBuffers(1, buffer, 0);
        indexBufferId = buffer[0];

        // Bind target
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);

        // Send the data from CPU to GPU
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertex_indices.length * 4,
                indexBuffer, GLES20.GL_STATIC_DRAW);
    }


    private void setupVertexBuffer() {

        //size
        vertexBuffer = BufferUtils.newFloatBuffer(vertices.length);
        //copy the content into vertex buffer
        ((FloatBuffer) vertexBuffer).put(vertices);

        vertexBuffer.position(0);

        //copy the vertices form cpu to gpu
        //generate the buffer object in gpu
        IntBuffer buffer = IntBuffer.allocate(1);
        GLES20.glGenBuffers(1, buffer);
        vertexBufferID = buffer.get(0);

        //bind with some binding point
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferID);

        //send data from cpu to gpu
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.length * 4,
                vertexBuffer, GLES20.GL_STATIC_DRAW);
    }

    public Matrix4f modelMatrix(){
        Matrix4f mat = new Matrix4f();
        mat.translate(position.x,position.y,position.z);
        mat.rotate(rotationX,1.0f,0.0f,0.0f);
        mat.rotate(rotationY,0.0f,1.0f,0.0f);
        mat.rotate(rotationZ,0.0f,0.0f,1.0f);
        mat.scale(scale,scale,scale);
        return mat;
    }

    public void setCamera(Matrix4f mat){
        camera.load(mat);
    }

    public void setProjection(Matrix4f mat){
        projection.load(mat);
    }

    public void draw(long dt) {

        //activate shader
        shader.begin();

        //Update camera matrix
        camera.multiply(modelMatrix());
        //enable model view matrix
        shader.setUniformMatrix("u_ModelViewMatrix",camera);

        shader.setUniformMatrix("u_ProjectionMatrix",projection);

        //enable input attribute
        shader.enableVertexAttribute("a_Position");
        shader.setVertexAttribute("a_Position", COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertex_indices.length, GLES20.GL_UNSIGNED_INT, 0);

        shader.disableVertexAttribute("a_Position");

        shader.end();
    }


}
