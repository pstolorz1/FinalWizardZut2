package com.example.tomek.magicwizards;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

;

/*! Glowna klasa generujaca glowny ekran z gra
 *
 *
 */
public class CustomGesturesActivity extends AI implements OnGesturePerformedListener
{
    private GestureLibrary gLibrary; /**< obiekt biblioteki gestow */
    private MyGLSurfaceView gLView; /**< kontrolka,na ktorej rysowane zostana obiekty openGL*/
    private TextView resultView; /**< kontrolka pokazujaca wynik wykonania gestu*/
    private TextView hpView; /**< wyswietla zycie gracza*/
    private TextView AIView; /**< wyswietla zycia komputera*/
    private TextView AIDamageView; /**< wyswietla obrazenia zadane przez komputer*/
    private TextView DamageView; /**< wyswietla obrazenia zadane przez gracza*/
    private ProgressBar hpBar_Player;
    private ProgressBar hpBar_Opponent;
    View test;
    int HP = 400;
    int HP_AI = 400;
    //private static long currTimeInMs = 0;
    //a


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Log.d("Test", "PRESTART");
        super.onCreate(savedInstanceState);
        //gLView = new MyGLSurfaceView(this);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //setContentView(gLView);
        //Log.d("Test", "START");
        gLView = findViewById(R.id.openGLOverlay);
        resultView = findViewById(R.id.resultText);
        AIView = findViewById(R.id.AIText);
        test = findViewById(R.id.gOverlay);
        AIDamageView = findViewById(R.id.AIDamageText);
        DamageView = findViewById(R.id.DamageText);
        hpBar_Player = findViewById(R.id.progressBar4);
        hpBar_Opponent = findViewById(R.id.progressBar6);
        hpBar_Opponent.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        hpBar_Opponent.setProgress(100);
        hpBar_Player.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        hpBar_Player.setProgress(100);
        test.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                // Interpret MotionEvent data
                /*if(System.currentTimeMillis() - currTimeInMs > 10000)
                {
                    //resultView.setText("CZAS: " + (System.currentTimeMillis() - currTimeInMs));
                    SetOpenGLCooldown();
                    gLView.myRenderer.AddNewEffect(new Vec2(event.getX(),event.getY()));
                }*/
                //resultView.setText("CZAS: " + (System.currentTimeMillis() - currTimeInMs));
                // Handle touch here
                //if()
                //resultView.setText("CZAS: " + SystemClock.uptimeMillis() % 4000L);
                gLView.myRenderer.ShowStar(event.getX(),event.getY());
                return true;

            }
        });
        gestureSetup();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        gLView.onPause();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        gLView.onResume();
    }
    //! Skonfigurowanie obslugi gestow
    /*!
    Wczytanie pliku z gestami, ustawienie kontrolki wyswietlajacej openGL
    */
    private void gestureSetup() {
        gLibrary =
                GestureLibraries.fromRawResource(this,
                        R.raw.gestures);
        if (!gLibrary.load())
        {
            //Log.d("Test", "FINISH");
            finish();
        }
        //Log.d("Test", "DALEJ");
        GestureOverlayView gOverlay = findViewById(R.id.gOverlay);
        gOverlay.addOnGesturePerformedListener(this);
    }
    //! Metoda obslugujaca rozpoznanie gestu
    /*!
    @param overlay kontrolka przechwytujaca gesty
    @param gesture przechwycony gest
    */
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        List<GestureStroke> gs = gesture.getStrokes();
        gLView.myRenderer.NewTrail(gs.get(0).points, gs.get(0).length);
        //resultView.setText(gs.get(0).length + " ");
        ArrayList<Prediction> predictions = gLibrary.recognize(gesture); /**< okresla podobienstwo z narysowanym przez gracza wzorem*/

        // TODO Zaleznoso miedzy "czarem", a progiem rozpoznawania (prediction.score)
        if (predictions.size() > 0 && predictions.get(0).score > 1.0)
        {
            //gLView.myRenderer.NewTrail(gs.get(0).points, gs.get(0).length);
            //resultView.setText("TEST: " + gLView.myRenderer.GetPointsDistance(new Vec2(2,5),new Vec2(5,9)));
            //String action = predictions.get(0).name + " " + predictions.get(0).score;
            Integer result = (int)(predictions.get(0).score);
            if(predictions.get(0).name == "kwadrat") result *= 5;
            else if(predictions.get(0).name == "trojkat") result *= 4;
            else result *= 15;
            //HP -= result;
            //hpView.setText(HP);
            HP_AI=HP_AI-result;
            resultView.setText("OBRAZENIA GRACZA: " + result.toString());
            DamageView.setText("HP PRZECIWNIKA: " + String.valueOf(HP_AI));
            hpBar_Opponent.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            hpBar_Opponent.setProgress(HP_AI/4);
            if(flag)
            {
                int hp_tmp=easy();
                AIView.setText("OBRAZENIA PRZECIWNIKA: "+ String.valueOf(hp_tmp));
                HP=HP-hp_tmp;
                AIDamageView.setText("HP GRACZA: " + String.valueOf(HP));
                hpBar_Player.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                hpBar_Player.setProgress(HP/4);
            }
            else{
                int hp_tmp=hard();
                AIView.setText("OBRAZENIA PRZECIWNIKA: " + String.valueOf(hp_tmp));
                HP=HP-hp_tmp;
                AIDamageView.setText("HP GRACZA: " + String.valueOf(HP));
                hpBar_Player.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                hpBar_Player.setProgress(HP/4);}
        }
        //changeHPonHit();
        if(HP<0)
            setContentView(R.layout.activity_lost);
        if(HP_AI<0)
            setContentView(R.layout.activity_win);

        else
        {
            // TODO Zmienio na Integer
            //resultView.setText("0");
        }


    }

    /*public static void SetOpenGLCooldown()
    {
        currTimeInMs = System.currentTimeMillis();
    }*/
    /* Funkcja odpowiedzialna za zmiane ikonki HP poszczegolnego gracza
    * imageView i ImageView2 odpowiedzialne za graczy
    * Brokenhp i else istnieje tylko dlatego, ze jest to exception przed crashem, jesli w jakis sposob wartosc zmiennej HP lub HP_AI jest... dzwina*/
   /* public void changeHPonHit(){
        ImageView imageView = (ImageView) findViewById(R.id.imageView3);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView4);

        if(HP > 250)
        {
            imageView.setImageResource(R.drawable.fullhp);
        }
        else if(HP >= 120 && HP <= 250){
            imageView.setImageResource(R.drawable.halfhp);
        }
        else if(HP <= 120){
            imageView.setImageResource(R.drawable.lowhp);
        }
        else{
            imageView.setImageResource(R.drawable.brokenhp);
        }

        if(HP_AI > 250)
        {
            imageView2.setImageResource(R.drawable.fullhp);
        }
        else if(HP_AI >= 120 && HP_AI <= 250){
            imageView2.setImageResource(R.drawable.halfhp);
        }
        else if(HP_AI <= 120){
            imageView2.setImageResource(R.drawable.lowhp);
        }
        else{
            imageView2.setImageResource(R.drawable.brokenhp);
        }
    }*/

}
