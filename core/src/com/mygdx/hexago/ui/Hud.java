package com.mygdx.hexago.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hexago.boardcore.CoreIO;
import com.mygdx.hexago.states.PlayState;


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
    private Table scoreTable;
    private Table turnTable;

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

        guiTable.add(playTable);
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
            guiTable.getCell(playTable).setActor(scoringTable);
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
            guiTable.getCell(scoringTable).setActor(playTable);
            playTable.clearChildren();
            playTable.add(turnTable).row();
            playTable.add(captureTable).row();
            playTable.add(playUndoButton).padTop(10).size(60, 40).row();
            playTable.add(passButton).padTop(10).size(60, 40).row();
        } catch( NullPointerException e ){
            System.out.println("Something is wrong. Already playing.");
        }

    }


    public BoardLog getBoardLog() { return boardLog; }
    public ChatField getChatField() { return chatField; }
    public BoardActor getBoardActor() { return boardActor; }
    public TextButton getPlayUndoButton() { return playUndoButton; }
    public TextButton getScoreUndoButton() { return scoreUndoButton; }
    public TextButton getPassButton() { return passButton; }
    public TextButton getDoneButton() { return doneButton; }
    public Label getWhiteCaptureLabel() { return whiteCaptureLabel; }
    public Label getBlackCaptureLabel() { return blackCaptureLabel; }
    public Label getWhiteScoreLabel() { return whiteScoreLabel; }
    public Label getBlackScoreLabel() { return blackScoreLabel; }
}
