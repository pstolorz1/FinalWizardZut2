package com.example.tomek.magicwizards;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.tomek.magicwizards.AI.flag;

//!  Klasa odpowiedzialna za gre z kouminacja
/*!
 */
public class SendingTest extends ChooseMenu implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gLibrary;
    private MyGLSurfaceView gLView;
    private TextView resultView;
    private TextView hpView;
    private TextView AIView;
    private TextView AIDamageView;
    private TextView DamageView;
    private TextView tmp;
    private ProgressBar hpBar_Player;
    private ProgressBar hpBar_Opponent;
    final DatabaseReference fbDb = FirebaseDatabase.getInstance().getReference();
    View test;
    int HP = 400;
    static int HP_AI = 400;
    int obrazenia_tmp=0;
    int odebrane_tmp=0;
    int abc=0;
    int tmp_id=0;
    int hp_tmp=400;
    static int flaga=0;

    //! Skonfigurowanie obslugi gestow
    /*!
    Wczytanie pliku z gestami, ustawienie kontrolki wyswietlajacej openGL
    */
    private void gestureSetup() {
        gLibrary =
                GestureLibraries.fromRawResource(this,
                        R.raw.gestures);
        if (!gLibrary.load()) {
            //Log.d("Test", "FINISH");
            finish();
        }
        //Log.d("Test", "DALEJ");
        GestureOverlayView gOverlay = findViewById(R.id.gOverlay);
        gOverlay.addOnGesturePerformedListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = findViewById(R.id.testSending);
        gLView = findViewById(R.id.openGLOverlay);
        resultView = findViewById(R.id.resultText);
        AIView = findViewById(R.id.AIText);
        test = findViewById(R.id.gOverlay);
        AIDamageView = findViewById(R.id.AIDamageText);
        DamageView = findViewById(R.id.DamageText);
        tmp = findViewById(R.id.textView6);
        hpBar_Player = findViewById(R.id.progressBar4);
        hpBar_Opponent = findViewById(R.id.progressBar6);

        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resultView.setText("OBRAZENIA GRACZA: " + String.valueOf(0));
                DamageView.setText("HP KOMPUTERA: " + String.valueOf(400));
                AIView.setText("OBRAZENIA KOMPUTERA: "+ String.valueOf(0));
                AIDamageView.setText("HP GRACZA: " + String.valueOf(400));
                tmp.setText(String.valueOf(100));
                String key = fbDb.child("ciosy").push().getKey();
                TestObject to = new TestObject(key, 0, rand_id, 400);
                fbDb.child("ciosy").child(key).setValue(to);
                HP_AI=400;
                hpBar_Opponent.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                hpBar_Opponent.setProgress(100);
                hpBar_Player.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                hpBar_Player.setProgress(100);
            }
        });


        test.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                gLView.myRenderer.ShowStar(event.getX(), event.getY());
                return true;

            }
        });
        gestureSetup();


    }

    @Override
    protected void onPause() {
        super.onPause();
        gLView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        gLView.onResume();

    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        List<GestureStroke> gs = gesture.getStrokes();
        gLView.myRenderer.NewTrail(gs.get(0).points, gs.get(0).length);
        //resultView.setText(gs.get(0).length + " ");

        ArrayList<Prediction> predictions = gLibrary.recognize(gesture); /**< okresla podobienstwo z narysowanym przez gracza wzorem*/

        // TODO Zaleznoso miedzy "czarem", a progiem rozpoznawania (prediction.score)
        if (predictions.size() > 0 && predictions.get(0).score > 1.0)
        {
            Integer result = (int) (predictions.get(0).score);
            if (predictions.get(0).name == "kwadrat") result *= 5;
            else if (predictions.get(0).name == "trojkat") result *= 4;
            else result *= 15;
            obrazenia_tmp=result;
            hp_tmp=hp_tmp-obrazenia_tmp;
            HP_AI = HP_AI - obrazenia_tmp;




        }
       // Toast.makeText(getApplicationContext(), "cyk", Toast.LENGTH_LONG).show();

        String key = fbDb.child("ciosy").push().getKey();
        TestObject to = new TestObject(key, obrazenia_tmp, rand_id, HP_AI);
        fbDb.child("ciosy").child(key).setValue(to);


        if(HP_AI<0){
            flaga=1;
            setContentView(R.layout.activity_win);
            }

           // Toast.makeText(getApplicationContext(), "cyk2", Toast.LENGTH_LONG).show();

        //!  Funkcja odpowiedzialna za zczytwanie i przypisywanie wartosci do bazy danych
        /*!
         */
             fbDb.child("ciosy").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long test = dataSnapshot.getChildrenCount();
                    Iterable<DataSnapshot> dejta = dataSnapshot.getChildren();
                    String temp = "";

                    for (DataSnapshot d : dejta) {
                        TestObject t = d.getValue(TestObject.class);
                        if(t.getid()!=rand_id){
                           // if(flaga==1)
                              //  setContentView(R.layout.activity_lost);
                        odebrane_tmp=t.val;
                        tmp_id=t.getid();
                        temp += "READ: " + t.val + " |\n";
                        AIView.setText("OBRAZENIA PRZECIWNIKA: " + String.valueOf(t.getVal()));
                        AIDamageView.setText("HP GRACZA: " + String.valueOf(t.gethp()));
                        hpBar_Player.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        hpBar_Player.setProgress(t.gethp()/4);
                        tmp.setText(String.valueOf(t.gethp()));
                        }





                    }
                    if (Integer.parseInt(tmp.getText().toString()) < 0)
                        setContentView(R.layout.activity_lost);
                    abc++;
                    HP = HP - odebrane_tmp;
                    odebrane_tmp=0;

                    //AIDamageView.setText("HP GRACZA: " + String.valueOf(HP));
                   // TextView t = findViewById(R.id.readDataText);
                   // t.setText("Liczba obiektow = " + test + "\n" + temp);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });






        resultView.setText("OBRAZENIA GRACZA: " + obrazenia_tmp);
        DamageView.setText("HP PRZECIWNIKA: " + String.valueOf(HP_AI));
        hpBar_Opponent.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        hpBar_Opponent.setProgress(HP_AI/4);
        //int hp_tmp = odebrane_tmp;
       // AIView.setText("OBRAZENIA KOMPUTERA: " + String.valueOf(to.val));

      //  if(!tmp.getText().toString().isEmpty()){
            if (Integer.parseInt(tmp.getText().toString()) < 0)
                setContentView(R.layout.activity_lost);
            if (HP_AI < 0)
                setContentView(R.layout.activity_win);
    }

}
