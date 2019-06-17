package com.example.tomek.magicwizards;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
//!  Klasa zajmujaca sie wyswietlaniem przy uzyciu openGL
/*! inicjalizacja openGL, rysowanie gwiazdek i obliczanie pozycji w przestrzeni openGL
*/
public class MyGLRenderer implements GLSurfaceView.Renderer
{
    //! gwiazda pojawiajaca sie co jakis czas podczas robienia gestu
    Star bigStar;
    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    //! macierz wynikowa
    private final float[] vPMatrix = new float[16];
    //! macierz projekcji
    private final float[] projectionMatrix = new float[16];
    //! macierz widoku
    private final float[] viewMatrix = new float[16];
    //! macierz rotacji/obrotu
    private float[] rotationMatrix = new float[16];
    //! zmienna przechowujaca kat
    public static volatile float mAngle;
    //! wysokoso i szerokoso ekranu
    public int screenHeight, screenWidth;
    //public float X, Y;
    //! pozycja duzej gwiazdy
    Vec2 starPos;
    //! liczba malych gwiazdek alokowana przy tworzeniu okna
    int instantiateNumber = 20;
    //! iloso gwiazdek do wyswietlenia
    int sqNumber = 20;
    //! zmienna pomocnicza, w ktorej przechowywana jest skala duzej gwiazdy
    float scaleFactor = 1.0f;
    //! zmienna pomocnicza, w ktorej przechowywana jest przezroczystoso(kanal alpha) duzej gwiazdy
    float alphaFactor = 0.0f;
    //! lista wspolrzednych malych gwiazdek
    public List<Vec2> trail = new ArrayList<>();
    //! lista obiektow (malych gwiazdek)
    public List<Star> symbols = new ArrayList<>();
    //! flaga sterujaca wyswietlaniem malych gwiazdek
    boolean showThem = false;
    //! flaga sterujaca wyswietlaniem duzej gwiazdki
    boolean showBigStar = false;

    //! Metoda tworzaca nowa linie z gwiazdek na podstawie wspolrzednych gestu
    /*!
      \param positions wspolrzedne punktow skladajacych sie na gest
      \param trailLength dlugoso gestu
    */
    public void NewTrail(float[] positions, float trailLength)
    {
        float distanceBetweenEffects = trailLength / instantiateNumber; /**< obliczamy dlugosc po ktorej umeiszczamy kolejny efekt*/
        //trail.clear();
        trail.get(0).Set(getWorldCoords(new Vec2(positions[0], positions[1]))); // poczatek

        float currUnusedLength = 0f;
        int effects = 1;
        for (int i = 2; i < positions.length; i=i+2)
        {
            Vec2 A = new Vec2(positions[i-2],positions[i-1]); /** <reszta odl. z poprzednich punktow + dystans miedzy tym pkt a poprzednim*/
            Vec2 B = new Vec2(positions[i],positions[i+1]);
            float distanceBetweenPoints = GetPointsDistance(A,B);
            currUnusedLength = currUnusedLength + distanceBetweenPoints;
            if(currUnusedLength > distanceBetweenEffects)
            {
                //  jest wiekszy od odleglosci miedzy kolejnymi efektami, oznacza to ze
                // juz nie czas na umieszczenie kolejnego efektu
                float d = distanceBetweenPoints - (currUnusedLength-distanceBetweenEffects);
                float Xac = d * (B.X()-A.X())/distanceBetweenPoints;
                float Yac = d * (B.Y()-A.Y())/distanceBetweenPoints;

                if(effects < instantiateNumber)trail.get(effects).Set(getWorldCoords(new Vec2(A.X()+Xac, A.Y()+Yac)));
                else break;

                effects++;
                currUnusedLength = (currUnusedLength-distanceBetweenEffects);
            }
        }
        if(effects <= instantiateNumber)sqNumber = effects;
        else sqNumber = instantiateNumber;
        showThem = true;
        showBigStar = false;
    }
    //! Metoda liczaca odlegloso miedzy punktami w przestrzeni
    /*!
      \param a punkt poczatkowy
      \param b punkt końcowy
      \return Odlegloso miedzy punktami
    */
    public float GetPointsDistance(Vec2 a, Vec2 b)
    {
        return (float)Math.sqrt(Math.pow((b.X()-a.X()), 2) + Math.pow((b.Y()-a.Y()), 2));
    }
    //! Metoda, ktora nakazuje rysowanie gwiazdy w danym punkcie
    /*!
      \param x wspolrzedna x gwiazdy
      \param y wspolrzedna y gwiazdy
    */
    public void ShowStar(float x, float y)
    {
        if(!showBigStar)
        {
            starPos = getWorldCoords(new Vec2(x,y));
            scaleFactor = 1.0f;
            alphaFactor = 0.0f;
            showBigStar = true;
        }
    }
    //! Metoda, w ktorej czyszczony jest ekran i inicjalizowane sa wszystkie uzywane poźniej obiekty
    /*!
    */
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 0.2f, 0.2f, 1.0f);
        //mSquare = ne
        bigStar = new Star(0.2f);
        //bigStar.setColor(0,0,1,0.5f);
        for (int i = 0; i < instantiateNumber; i++)
        {
            symbols.add(new Star(0.1f));
            //symbols.get(i).setColor(0, (i * 0.015f),0);
            trail.add(new Vec2(0,0));
        }
    }

    //! Metoda, zajmujaca sie rysowaniem pojedyńczej klatki
    /*!
      \param gl nie uzywany parametr
    */
    public void onDrawFrame(GL10 gl)
    {
        // enable transparency
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDepthMask(false);
        //

        float[] scratch = new float[16];
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        if(showThem)
        {
            for (int i = 0; i < sqNumber; i++)
            {
                Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
                Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);
                Matrix.translateM(viewMatrix, 0, trail.get(i).X(), trail.get(i).Y(), 0);
                Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
                Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
                symbols.get(i).draw(scratch);
                //mSquare.draw(scratch);
            }
        }
        if(showBigStar)
        {
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);
            Matrix.translateM(viewMatrix, 0, starPos.X(), starPos.Y(), 0);
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            Matrix.scaleM(viewMatrix, 0, scaleFactor, scaleFactor, 0);
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
            bigStar.setAlpha(1-alphaFactor);
            bigStar.draw(scratch);
            scaleFactor += 0.20f;
            alphaFactor += 0.04f;
            if(alphaFactor > 0.9f) showBigStar = false;
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        screenHeight = height;
        screenWidth = width;
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
    //! Metoda, wczytujaca shader
    /*!
      \param type typ shadera
      \param shaderCode treso shadera
    */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    //! Metoda, przeksztalcajaca wspolrzedne dotyku na wspolrzedne w przestrzeni openGL
    /*!
      \param touch wspolrzedna punktu na ekranie
      \return Punkt w przestrzeni openGL
    */
    public Vec2 getWorldCoords( Vec2 touch)
    {
        Vec2 worldPos = new Vec2(0,0);

        float screenW = screenWidth;
        float screenH = screenHeight;
        float ratio = screenW/screenH;

        worldPos.SetX(touch.X()/screenW * ratio);
        worldPos.SetY(touch.Y() / screenH);

        worldPos.Set(worldPos.X() * -2 + ratio,worldPos.Y() * -2 + 1 );

        return worldPos;
    }

}