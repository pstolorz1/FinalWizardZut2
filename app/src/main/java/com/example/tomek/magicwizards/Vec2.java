package com.example.tomek.magicwizards;
//!  Klasa pomocnicza definiująca punkt w przestrzeni
/*!
  Jedyne pola to współrzędne x i y typu float
*/
public class Vec2
{
    private float x,y;
    public Vec2(float xPos,float yPos)
    {
        x = xPos;
        y = yPos;
    }
    public float X()
    {
        return x;
    }
    public float Y()
    {
        return y;
    }
    public void Set(float xPos,float yPos)
    {
        x = xPos;
        y = yPos;
    }
    public void Set(Vec2 v)
    {
        x = v.X();
        y = v.Y();
    }
    public void SetX(float xPos)
    {
        x = xPos;
    }
    public void SetY(float yPos)
    {
        y = yPos;
    }
    //! Nadpisanie metody toString dla tej kalsy
    /*!
    /return string w formacie X: zmiennaX Y: zmiennaY
    */
    public String toString()
    {
        return "X: " + x + " " + "Y: " + y;
    }
}
