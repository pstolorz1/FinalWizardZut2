package com.example.tomek.magicwizards;

public class TestObject
{
    public TestObject(String uuid, int val, int id, int hp)
    {
        this.uuid = uuid;
        this.val = val;
        this.id = id;
        this.hp = hp;
    }
    public TestObject(){}

    public int getVal() {return val;}
    public void setVal(int val) {this.val = val;}

    int val;

    public String getUuid() {return uuid;}
    public void setUuid(String uuid) {this.uuid = uuid;}
    String uuid;

    public int getid() {return id;}
    public void setid(int id) {this.id = id;}
    int id;

    public int gethp() {return hp;}
    public void sethp(int hp) {this.hp = hp;}
    int hp;
}
