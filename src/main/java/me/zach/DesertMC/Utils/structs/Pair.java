package me.zach.DesertMC.Utils.structs;

public class Pair<F, S> {
    public F first;
    public S second;
    public Pair(F first, S second){
        this.first = first;
        this.second = second;
    }

    public String toString(){
        return "Pair{" +
                first +
                ", " + second +
                '}';
    }
}