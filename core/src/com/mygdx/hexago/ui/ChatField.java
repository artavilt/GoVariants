package com.mygdx.hexago.ui;


import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class ChatField extends TextField {

    public ChatField(Skin skin){
        super("", skin);
        this.setAlignment(Align.left);
    }

}
