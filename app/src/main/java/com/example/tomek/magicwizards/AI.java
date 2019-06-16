package com.example.tomek.magicwizards;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

//!  Klasa reprezentujaca poziomy sztucznej inteligencji
/*!
 */
public class AI extends AppCompatActivity {

    //! Flaga, ktora ustawia poziom trudnosci
    static public boolean flag;
    TextView view_data;

    /** \brief Latwy poziom AI
     *
     */
    Integer easy()
    {
        //! Wartosci, ktore moze losowac komputer w zaleznosci od poziomu trudnosci
        int x = (int)(Math.random() * 50 + 10); /**< Wartosci, ktore moze losowac komputer w zaleznosci od poziomu trudnosci*/
        return(x);
    }

    /** \brief Trudny poziom AI
     *
     */
    Integer hard()
    {
        //! Wartosci, ktore moze losowac komputer w zaleznosci od poziomu trudnosci
        int x = (int)(Math.random() * 80 + 80); /**< Wartosci, ktore moze losowac komputer w zaleznosci od poziomu trudnosci*/
        return(x);
    }


}
