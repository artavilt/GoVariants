package com.mygdx.hexago.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.hexago.boardcore.BoardCore;
import com.mygdx.hexago.boardcore.TileState;
import com.mygdx.hexago.handlers.GameStateManager;
import com.mygdx.hexago.main.Game;
import com.mygdx.hexago.ui.*;


public abstract class PlayState extends GameState {
    private static final String BACKGROUND = "res/board/background.png";

    protected Stage stage;
    protected Skin skin;
    protected Image background;
    protected BoardActor boardActor;
    protected BoardLog boardLog;
    protected ChatField chatField;
    protected TextButton passButton;
    protected TextButton playUndoButton;
    protected TextButton scoreUndoButton;
    protected TextButton doneButton;
    protected Hud hud;
    protected BoardCore boardCore;

    protected PlayState(GameStateManager gsm){
        super(gsm);
        stage = new Stage(new ScreenViewport(game.getCam()));
        skin = gsm.getSkin();
        background = new Image(new Texture(BACKGROUND));



        Table rootTable = new Table();
        rootTable.setFillParent(true);
        int boardRadius = gsm.getBoardConfig().boardRadius;
        hud = new Hud( this, boardRadius );
        boardCore = new BoardCore( boardRadius, TileState.BLACK );
        boardActor = hud.getBoardActor();
        boardActor.setBoardCore(boardCore);
        boardLog = hud.getBoardLog();
        boardCore.setBoardIO(boardActor);
        boardCore.setLogIO(boardLog);
        boardCore.setWhiteCaptureIO(new CaptureLabel(hud.getWhiteCaptureLabel(), TileState.WHITE));
        boardCore.setBlackCaptureIO(new CaptureLabel(hud.getBlackCaptureLabel(), TileState.BLACK));
        boardCore.setWhiteScoreIO(new ScoreLabel(hud.getWhiteScoreLabel(), TileState.WHITE));
        boardCore.setBlackScoreIO(new ScoreLabel(hud.getBlackScoreLabel(), TileState.BLACK));
        boardCore.setTurnIO(new TurnTile(boardActor.getTurnTile()));
        boardCore.setCoreIO( hud );

        chatField = hud.getChatField();
        playUndoButton = hud.getPlayUndoButton();
        passButton = hud.getPassButton();
        scoreUndoButton = hud.getScoreUndoButton();
        doneButton = hud.getDoneButton();

        hud.left().bottom();
        hud.setDebug(true);
        stage.addActor(hud);

        resize(Game.V_WIDTH, Game.V_HEIGHT);
        focus();
    }

    public Skin getSkin(){ return skin; }

    public BoardActor getBoardActor(){ return boardActor; }

    public abstract void initialize();

    public abstract void turnEvent( int x, int y );

    public void resize( int width, int height ) {
        stage.getViewport().update(width, height);
        hud.resize(width, height);
        focus();
    }

    public void update(float dt) {}

    public void render() {
        Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        stage.act();
        stage.draw();
    }

    public void focus(){
        Gdx.input.setInputProcessor(stage);
    }

    public void dispose() {
        boardActor.dispose();
    }

    public void lock(){  }

    public void unlock(){ }
}
