package com.mygdx.hexago.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.hexago.boardcore.BoardCore;
import com.mygdx.hexago.boardcore.TileState;
import com.mygdx.hexago.handlers.GameStateManager;
import com.mygdx.hexago.main.Game;
import com.mygdx.hexago.network.UndoPacket;
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
    protected TextButton forwardButton;
    protected TextButton backButton;
    protected TextField turnEntry;
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

        hud.getForwardButton().addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if( boardCore.getShownTurn() + 1 < boardCore.size() ){
                    boardCore.showTurn(boardCore.getShownTurn()+1);
                }
            }
        });

        hud.getBackButton().addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if( boardCore.getShownTurn() > 0 ){
                    boardCore.showTurn(boardCore.getShownTurn()-1);
                }
            }
        });

        TextField turnEntry = hud.getTurnEntry();
        turnEntry.setTextFieldFilter(new TextField.TextFieldFilter() {
            public boolean acceptChar(TextField textField, char c) {
                if (!Character.isDigit(c)) {
                    return false;
                } else {
                    int cur = Integer.parseInt(textField.getText() + c);
                    return (cur > 0 && cur < boardCore.size());
                }
            }
        });

        turnEntry.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent e, int keyCode) {
                if (keyCode == Input.Keys.ENTER) {
                    boardCore.showTurn(Integer.parseInt(hud.getTurnEntry().getText()));
                }
                return false;
            }
        });

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

    public void lock(){ boardActor.lock(); }

    public void unlock(){ boardActor.unlock(); }

    public void doneDialog(){
        Dialog doneDialog = new Dialog("Game Finished", skin);
        TextButton reviewButton = new TextButton("Review", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        String message = "";
        int whiteScore = boardCore.currentTurn().getWhiteScore();
        int blackScore = boardCore.currentTurn().getBlackScore();
        if( whiteScore > blackScore ){
            message += boardLog.getWhiteName() + " wins by " + (whiteScore - blackScore) + " points!";
        } else if( blackScore > whiteScore ){
            message += boardLog.getBlackName() + " wins by " + (blackScore - whiteScore) + " points!";
        } else {
            message += "Tie game!";
        }
        doneDialog.text(message);
        doneDialog.button(reviewButton);
        doneDialog.button(exitButton);
        lock();
        doneDialog.show(stage);
        exitButton.addListener( new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                gsm.setState(GameStateManager.GUIMENU);
            }
        });
        reviewButton.addListener( new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                hud.setReviewing();
                gsm.getServer().close();
                gsm.getClient().close();
            }
        });
    }
}
