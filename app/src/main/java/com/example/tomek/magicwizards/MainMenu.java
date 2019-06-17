package com.example.tomek.magicwizards;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

//!  Klasa obslugujaca menu
/*!
 */
public class MainMenu extends AppCompatActivity {

    private Button pressPlay; /**< guzik do wlaczenia gry*/
    private Button pressCredits; /**< guzik do pokazania tworcow gry*/


    private static final int REQUEST_CODE_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        pressPlay = (Button) findViewById(R.id.button2);
        pressPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View x){
                openMainMenu();
            }
        });

        pressCredits = (Button) findViewById(R.id.button);
        pressCredits.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View x)
            {
                openCredits();
                /*if (FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    openCredits();
                }
                else {

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            REQUEST_CODE_SIGN_IN);
                }*/
            }
        });
    }
    /*!  Funkcja przenoszaca z jednego activity do drugiego
     *
     */
    public void openMainMenu(){
        Intent intent = new Intent(this, ChooseMenu.class);
        startActivity(intent);
    }
    /**! Funkcja przenoszaca z jednego activity do drugiego
     *
     */
    public void openCredits()
    {
        Intent intent = new Intent(this, CreditsScene.class);
        startActivity(intent);
        //Intent intent = new Intent(this, CreditsScene.class);
        //startActivity(intent);
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                // Successfully signed in
                openCredits();
            }
            else {
                //Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SendingTest.class);
                startActivity(intent);
            }
        }
    }*/

}
