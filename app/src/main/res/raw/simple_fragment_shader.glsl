// Set the default precision to medium, don't need as high precision in the fragment shader.
precision mediump float;

// The entry point for our fragment shader.
void main() {
    // Pass through the color
    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0); //white color
}