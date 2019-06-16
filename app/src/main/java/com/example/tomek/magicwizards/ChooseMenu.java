package com.example.tomek.magicwizards;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ChooseMenu extends AI {
    private Button Computer;
    private Button Communiaction;
    static Integer rand_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Computer = (Button) findViewById(R.id.button5);
        Computer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View x){
                computer();
            }
        });

        Communiaction = (Button) findViewById(R.id.button6);
        Communiaction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View x){
                communication();
            }
        });
    }

    Integer losuj_id()
    {
        //! Wartosci, ktore moze losowac komputer w zaleznosci od poziomu trudnosci
        int x = (int)(Math.random() * 1000 + 1); /**< Wartosci, ktore moze losowac komputer w zaleznosci od poziomu trudnosci*/
        return(x);
    }

    public void computer()
    {
        Intent intent = new Intent(this, ConnectMenu.class);
        startActivity(intent);
    }

    public void communication()
    {
        //Toast.makeText(getApplicationContext(), "Komunikacja", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SendingTest.class);
        startActivity(intent);
        rand_id=losuj_id();
    }
}
