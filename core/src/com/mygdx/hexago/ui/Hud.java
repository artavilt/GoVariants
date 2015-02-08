package com.mygdx.hexago.ui;


import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hexago.Localisation;
import com.mygdx.hexago.boardcore.CoreIO;
import com.mygdx.hexago.boardcore.TileState;
import com.mygdx.hexago.states.PlayState;

import java.util.Stack;


public class Hud extends Table implements CoreIO {
    private BoardLog boardLog;
    private BoardActor boardActor;
    private ChatField chatField;
    private Table guiTable;
    private TextButton playUndoButton;
    private TextButton passButton;
    private Label whiteCaptureLabel;
    private Label blackCaptureLabel;
    private Label whiteScoreLabel;
    private Label blackScoreLabel;
    private Table playTable;
    private Table scoringTable;
    private TextButton scoreUndoButton;
    private TextButton doneButton;
    private Table captureTable;
    private Table navigateTurnTable;
    private Table scoreTable;
    private Table turnTable;
    private Table reviewTable;
    private TextButton forwardButton;
    private TextButton backButton;
    private TextField turnEntry;

    private Cell controlCell;

    public Hud(PlayState ps, int boardRadius){
        super(ps.getSkin());
        Skin skin = ps.getSkin();
        this.defaults().expand().fill();

        boardActor = new BoardActor(ps, boardRadius);
        boardLog = new BoardLog(skin);
        guiTable = new Table(skin);
        playTable = new Table(skin);
        scoringTable = new Table(skin);
        captureTable = new Table(skin);
        whiteCaptureLabel = new Label("White: 0", skin);
        blackCaptureLabel = new Label("Black: 0", skin);
        scoreTable = new Table(skin);
        whiteScoreLabel = new Label("White: 0", skin);
        blackScoreLabel = new Label("Black: 0", skin);
        turnTable = new Table(skin);
        chatField = new ChatField(skin);
        playUndoButton = new TextButton("Undo", skin);
        passButton = new TextButton("Pass", skin);
        scoreUndoButton = new TextButton("Undo", skin);
        doneButton = new TextButton("Done", skin);
        forwardButton = new TextButton("+1", skin);
        backButton = new TextButton("-1", skin);
        reviewTable = new Table(skin);
        navigateTurnTable = new Table(skin);
        turnEntry = new TextField("", skin);

        this.add(boardActor);
        this.add(guiTable).right();

        guiTable.top();
        guiTable.add(boardLog).row();
        guiTable.add(chatField).left().row();

        captureTable.add("Captures:").padTop(10).row();
        captureTable.add(whiteCaptureLabel).pad(0, 0, 0, 10);
        captureTable.add(blackCaptureLabel).pad(0, 10, 0, 0);

        scoreTable.add("Score: ").row();
        scoreTable.add(whiteScoreLabel).pad(0, 0, 0, 10);
        scoreTable.add(blackScoreLabel).pad(0, 10, 0, 0);

        turnTable.add("Turn:  ");
        turnTable.add(boardActor.getTurnTile().getImage()).size(60, 60);

        navigateTurnTable.add("Navigate Game: ").row();
        navigateTurnTable.add(backButton).pad(0, 0, 0, 10);
        navigateTurnTable.add(turnEntry).width(60);
        navigateTurnTable.add(forwardButton).pad(0, 10, 0, 0);

        controlCell = guiTable.add(playTable);
        playTable.add(turnTable).row();
        playTable.add(captureTable).row();
        playTable.add(playUndoButton).padTop(10).size(60, 40).row();
        playTable.add(passButton).padTop(10).size(60, 40).row();

    }

    public void resize(int width, int height ){
        this.setBounds(0, 0, width, height);
        Array<Cell> cells = this.getCells();
        float boardHeight = width - height < 250 ? width - 250 : height;

        cells.get(0).left().expand().fill().size(boardHeight);
        cells.get(1).left().expand().fill().size(width - boardHeight, height);
        cells = guiTable.getCells();
        cells.get(0).size(width - boardHeight, height - 400).fill().top();
        cells.get(1).fillX().top();
    }

    public void setScoring(){
        try{
            controlCell.setActor(scoringTable);
            scoringTable.clearChildren();
            scoringTable.add(scoreTable).row();
            scoringTable.add(captureTable).row();
            scoringTable.add(scoreUndoButton).padTop(10).size(60, 40).row();
            scoringTable.add(doneButton).padTop(10).size(60, 40).row();
        } catch( NullPointerException e ){
            System.out.println("Something is wrong. Already scoring.");
        }
    }

    public void setPlaying(){
        try{
            controlCell.setActor(playTable);
            playTable.clearChildren();
            playTable.add(turnTable).row();
            playTable.add(captureTable).row();
            playTable.add(playUndoButton).padTop(10).size(60, 40).row();
            playTable.add(passButton).padTop(10).size(60, 40).row();
        } catch( NullPointerException e ){
            System.out.println("Something is wrong. Already playing.");
        }

    }

    public void setReviewing(){
        controlCell.setActor(reviewTable);
        reviewTable.clearChildren();
        reviewTable.add( turnTable ).row();
        reviewTable.add( captureTable ).row();
        reviewTable.add( navigateTurnTable );
    }

    public void setWhiteCaptures( int captures ){
        whiteCaptureLabel.setText(Localisation.WHITE + ": " + captures);
    }

    public void setBlackCaptures( int captures ){
        blackCaptureLabel.setText(Localisation.BLACK + ": " + captures);
    }

    public void setBlackName( String name ){
        boardLog.setBlackName(name);
    }

    public void setWhiteName(String name ){
        boardLog.setWhiteName(name);
    }

    public void setWhiteScore( int score ){
        whiteScoreLabel.setText(Localisation.WHITE + ": " + score);
    }

    public void setBlackScore(int score){
        blackScoreLabel.setText(Localisation.BLACK + ": " + score);
    }

    public void gameMessage(String entry, int turn){
        boardLog.gameMessage(entry, turn);
    }

    public void playerMessage(String entry, int turn){
        boardLog.playerMessage(entry, turn);
    }

    public String getWhiteName(){ return boardLog.getWhiteName(); }

    public String getBlackName(){ return boardLog.getBlackName(); }

    public void updateTurn( int turn ){
        boardActor.getTurnTile().update( turn );
    }

    public void calibrateTiles(Stack<TileState> changes){
        boardActor.calibrateTiles(changes);
    }

    public BoardLog getBoardLog() { return boardLog; }
    public ChatField getChatField() { return chatField; }
    public BoardActor getBoardActor() { return boardActor; }
    public TextButton getPlayUndoButton() { return playUndoButton; }
    public TextButton getScoreUndoButton() { return scoreUndoButton; }
    public TextButton getPassButton() { return passButton; }
    public TextButton getDoneButton() { return doneButton; }
    public TextButton getForwardButton() { return forwardButton; }
    public TextButton getBackButton(){ return backButton; }
    public TextField getTurnEntry(){ return turnEntry; }
}
