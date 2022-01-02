package me.zach.DesertMC.Utils.ActionBar;

public class SimpleActionBar extends ActionBar {
    String content;
    public SimpleActionBar(String content){
        this.content = content;
    }

    public String getMessage(){
        return content;
    }
}
