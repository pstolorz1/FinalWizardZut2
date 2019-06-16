package com.example.tomek.magicwizards;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

//!  Klasa definiujaca gwiazdkę z wykorzystaniem openGL
/*!
  Definiowanie kształtu (współrzędne wierzchołków), koloru, shadera
*/
public class Star
{
    //! bufor wierzchołków
    private FloatBuffer vertexBuffer;
    //! bufor listy trójkątów, z których zbudowny jest kwadrat
    private ShortBuffer drawListBuffer;
    private int mProgram;
    private int positionHandle;
    private int colorHandle;
    private float colorR,colorG,colorB,colorA;


    // number of coordinates per vertex in this array
    //! ilość współrzędnych określających jeden wierzchołek
    final int COORDS_PER_VERTEX = 3;
    //! tablica współrzędnych wierzchołków
    float squareCoords[] = {
            0.0f,  0.5f, 0.0f,   // top top
            -0.1f, 0.2f, 0.0f,   // top left
            0.1f, 0.2f, 0.0f,   // top righh
            0.15f, 0.0f, 0.0f,  // middle right
            0.4f, 0.2f, 0.0f,    // right
            0f, -0.1f,0.0f,     // middle
            0.25f,-0.25f,0.0f,      // bottom right
            -0.25f,-0.25f,0.0f,      // bottom left
            -0.15f,0.0f,0.0f,      // middle left
            -0.4f, 0.2f, 0.0f    // right
            };
    //! tablcia indexów tablicy squareCoords, które tworzą trojkąty
    private short drawOrder[] = { 0, 1, 2,  1, 3, 2,   2,3,4,   3,5,6,  8,5,3,  8,7,5,  1,8,3,   9,8,1}; // order to draw vertices
    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Use to access and set the view transformation
    private int vPMatrixHandle;


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    //! Konstruktor
    /*!
    /param scale skala gwiazdki
     */
    public Star(float scale)
    {
        initialSettings(scale);

    }
    //! Metoda ustawiająca kolor gwiazdki
    /*!
    /param R współczynnik R - czerwony
    /param G współczynnik G - zielony
    /param B współczynnik B - niebieski
    /param A współczynnik A - przezroczystość
    */
    public void setColor(float R,float G,float B, float A)
    {
        /*if(R >= 1.0f) R = 1.0f;
        if(G >= 1.0f) G = 1.0f;
        if(B >= 1.0f) B = 1.0f;*/
        colorR = R;
        colorG = G;
        colorB = B;
        colorA = A;
    }
    //! Metoda ustawiająca wspołczynnik alpha gwiazdki
    /*!
    /param A współczynnik A - przezroczystość
    */
    public void setAlpha(float A)
    {
        colorA = A;
    }
    //! Bezargumentowy kontruktor, domyślnie ustawiający skalę na 1.0
    /*!
    */
    public Star()
    {
        initialSettings(1.0f);
    }
    //! Metoda ustawiająca parametry openGl do wyświetlania gwiazdki
    /*!
    */
    public void initialSettings(float scale)
    {
        for (int i = 0; i < squareCoords.length; i++)
        {
            squareCoords[i] *= scale;
        }
        colorR = 1.0f;
        colorG = 1.0f;
        colorB = 0.0f;
        colorA = 1.0f;
        //Log.d("TEST",Float.toString(squareCoords[0]));
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        //SHADER
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }
    //! Metoda rysująca gwiazdkę
    /*!
    /param mvpMatrix macierz wynikowa (połączenie maceirzy projekcji i widoku)
    */
    public void draw(float[] mvpMatrix)
    {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Set color for drawing the triangle
        float[] color = {colorR,colorG,colorB,colorA};
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // TRANSLATION
        //float[] transMatrix = new float[16];
        // ------ tutaj

        //Matrix.setIdentityM(transMatrix,0);
        //Matrix.translateM(transMatrix,0,offsetX,offsetY,0);
        //Matrix.multiplyMM(transMatrix,0,mvpMatrix,0,transMatrix,0);
        //GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, transMatrix, 0);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);


        // Draw the triangle
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);


    }


}

