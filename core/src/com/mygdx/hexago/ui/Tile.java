package com.mygdx.hexago.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.hexago.boardcore.TileState;


public class Tile{
    private final static String BASE = "base";
    private final static String WHITE = "white";
    private final static String BLACK = "black";
    private final static String WHITEDEAD = "whitedead";
    private final static String BLACKDEAD = "blackdead";
    private final static String WHITESCORE = "whitescore";
    private final static String BLACKSCORE = "blackscore";

    private TextureAtlas textureAtlas;
    private Image sprite;

    public Tile(TextureAtlas textureAtlas, int x, int y){
        this.textureAtlas = textureAtlas;
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(BASE);
        this.sprite = new Image(region);
        sprite.setPosition(x, y);
    }

    public Tile(TextureAtlas textureAtlas){
        this.textureAtlas = textureAtlas;
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(BASE);
        this.sprite = new Image(region);
    }

    public Image getImage(){ return sprite; }

    public void setHeight(float height){
        sprite.setSize(height, height);
    }

    public void setPosition(float x, float y){
        sprite.setPosition(x, y);
    }

    public void update(int state){
        String stateTag;
        if( state == TileState.BASE ){ stateTag = BASE; }
        else if( state == TileState.BLACK ){ stateTag = BLACK; }
        else if( state == TileState.WHITE ){ stateTag = WHITE; }
        else if( state == TileState.BLACKDEAD ){ stateTag = BLACKDEAD; }
        else if( state == TileState.WHITEDEAD ){ stateTag = WHITEDEAD; }
        else if( state == TileState.WHITESCORE ){ stateTag = WHITESCORE; }
        else if( state == TileState.BLACKSCORE ){ stateTag = BLACKSCORE; }
        else stateTag = BASE;
        sprite.setDrawable(new TextureRegionDrawable(textureAtlas.findRegion(stateTag)));
    }

    public float getX(){
        return sprite.getX();
    }

    public float getY(){
        return sprite.getY();
    }

    public void draw( Batch sb){
        sprite.draw(sb, 1);
    }
}
