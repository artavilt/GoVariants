package com.mygdx.hexago.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.hexago.boardcore.*;
import com.mygdx.hexago.main.Game;
import com.mygdx.hexago.states.PlayState;

import java.util.Stack;

public class BoardActor extends Actor implements BoardIO {
    private static final String TILE_ATLAS = "res/tiles/tiles.atlas";
    private static final String SELECTOR = "selector";
    private static final float sqrt3 = 1.7320508F;
    private static final float MARGIN = 7/8F;
    private static final float XADJUST = 5;

    PlayState ps;

    private float tileHeight;
    private float offunit;

    private float xoff;
    private float yoff;
    private int boardRadius;
    private int boardDiameter;

    private TextureAtlas textureAtlas;
    private Sprite selector;

    Tile[][] tiles;

    BoardCore boardCore;

    Tile mouseTile;
    Tile turnTile;


    private int myTurn;

    private boolean lock;



    public BoardActor(PlayState ps, int boardRadius){
        super();
        this.ps = ps;
        //tileHeight should be a multiple of 4.
        this.textureAtlas = new TextureAtlas(TILE_ATLAS);
        selector = new Sprite(textureAtlas.findRegion(SELECTOR));
        this.myTurn = TileState.BASE;

        boardDiameter = 2 * boardRadius - 1;
        this.boardRadius = boardRadius;

        tiles = new Tile[boardDiameter][boardDiameter];
        for( int i = 0; i < boardDiameter; i++ ){
            for( int j = 0; j < boardDiameter; j++ ){
                if( i + j > boardRadius - 2 && i + j < 3 * boardRadius - 2){
                    tiles[i][j] = new Tile(textureAtlas);
                }
            }
        }


        boardCore = new BoardCore(boardRadius, TileState.BLACK);
        turnTile = new Tile( textureAtlas );
        lock = false;
        addListener( new BoardInputListener() );
    }

    public void positionChanged(){
        float x = this.getX();
        float y = this.getY();
        int boardRadius = (tiles.length+1)/2;
        int boardDiameter = tiles.length;

        for( int i = 0; i < boardDiameter; i++ ){
            for( int j = 0; j < boardDiameter; j++ ){
                if( i + j > boardRadius - 2 && i + j < 3 * boardRadius - 2){
                    float xpos = x + xoff + ((int) ((i - j)*offunit));
                    float ypos = y + yoff + ((i+j)*3*tileHeight)/4;
                    tiles[i][j].setPosition(xpos, ypos);
                }
            }
        }
    }

    public void sizeChanged(){
        float x = this.getX();
        float y = this.getY();
        float height = this.getHeight();

        tileHeight = ( 4 * sqrt3 * height * MARGIN / (6 * boardDiameter ));
        offunit = ( ( sqrt3 / 4F ) * tileHeight );
        xoff = ( 2 * offunit * ( boardRadius - 1 ) );
        xoff += height * ( 1 - MARGIN ) / 2;
        xoff += XADJUST;
        yoff = -( 3 * ( boardRadius - 1 ) ) * tileHeight / 4;
        yoff += height * ( 1 - MARGIN ) / 2;
        yoff += height * ( 1 - sqrt3 / 2F ) / 2F;
        for( int i = 0; i < boardDiameter; i++ ){
            for( int j = 0; j < boardDiameter; j++ ){
                if( i + j > boardRadius - 2 && i + j < 3 * boardRadius - 2){
                    float xpos = x + xoff + ((int) ((i - j)*offunit));
                    float ypos = y + yoff + ((i+j)*3*tileHeight)/4;
                    tiles[i][j].setHeight(tileHeight);
                    tiles[i][j].setPosition(xpos, ypos);
                }
            }
        }
        selector.setSize(tileHeight, tileHeight);
    }


    public void setMyTurn( int myTurn ){
        this.myTurn = myTurn;
    }

    public int getMyTurn(){ return myTurn; }

    public int getOppTurn(){ return myTurn == TileState.WHITE ? TileState.BLACK : TileState.WHITE; }

    public boolean isMyTurn(){
        return ( myTurn == boardCore.getTurn() || myTurn == TileState.BASE );
    }

    private int[] getMouseIndex(){
        float mx = Gdx.input.getX() - getX() - xoff;
        float my = Game.V_HEIGHT - Gdx.input.getY() - getY() - yoff;
        mx = mx / offunit;
        my = 4 * my / ( tileHeight );

        int ybar =  (int) Math.floor(my);
        if ( ybar % 3 != 0 ){
            ybar /= 3;
        } else {
            int xtype = ((int) Math.floor(mx)) % 2;
            int ytype = ( ybar % 6 ) / 3;
            float xd = mx % 1;
            if( mx < 0 ) { xd = 1 - Math.abs(xd); }
            float yd = my % 1;
            if( ( xtype == 0 && ytype == 0 ) || ( xtype == 1 && ytype == 1 ) ){
                xd = 1 - xd;
            }
            if( yd > xd ){
                ybar = (ybar + 1)/3;
            } else { ybar = ( ybar - 1 )/3; }
        }
        int row =  (int) Math.floor( (mx+ybar) / 2);
        int[] ret = { row, ybar - row };
        return ret;
    }

    public void attemptPlay( int x, int y ){
        if( boardCore.input(x, y) ){
            ps.turnEvent( x, y );
        }
    }

    public void calibrateTiles(Stack<TileState> changes ){
        while ( ! changes.isEmpty() ) {
            TileState tilestate = changes.pop();
            Tile s = tiles[tilestate.x][tilestate.y];
            s.update(tilestate.state);
        }
    }

    public void lock(){ lock = true; }

    public void unlock(){ lock = false; }

    public Tile getTurnTile(){
        return this.turnTile;
    }

    public void draw(Batch sb, float parentalpha){
        for( Tile[] i: tiles ){
            for( Tile t: i){
                if( t != null ){
                    t.draw(sb);
                }
            }
        }
        if( mouseTile != null ){
            selector.setPosition( mouseTile.getX(), mouseTile.getY() );
            selector.draw(sb);
        }
    }

    public void dispose(){
        textureAtlas.dispose();
    }

    public void setBoardCore(BoardCore boardCore ){ this.boardCore = boardCore; }

    public class BoardInputListener extends InputListener{
        @Override
        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }

        @Override
        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            if( !lock && (isMyTurn() || boardCore.isScoring()) ){
                int[] ind = getMouseIndex();
                attemptPlay(ind[0], ind[1]);
            }
            super.touchUp(event, x, y, pointer, button);
        }

        @Override
        public boolean mouseMoved( InputEvent event, float x, float y){
            if( isMyTurn() ){
                int[] ind = getMouseIndex();
                try {
                    Tile tile = tiles[ind[0]][ind[1]];
                    if( tile != null ) {
                        mouseTile = tiles[ind[0]][ind[1]];
                    }
                } catch( ArrayIndexOutOfBoundsException e ){
                    mouseTile = null;
                }
            }
            return super.mouseMoved(event, x, y);
        }
    }
}


