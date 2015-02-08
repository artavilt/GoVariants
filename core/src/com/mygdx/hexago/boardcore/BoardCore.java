package com.mygdx.hexago.boardcore;


import java.util.Stack;

public class BoardCore extends Stack<BoardState> {
    //private LogIO logIO;
    //private CaptureIO whiteCaptureIO;
    //private CaptureIO blackCaptureIO;
    //private BoardIO boardIO;
    //private ScoreIO whiteScoreIO;
    //private ScoreIO blackScoreIO;
    private CoreIO coreIO;

    private boolean lastTurnWasPass;
    private boolean scoring;
    private int showTurn;

    public BoardCore(int boardRadius, int turn){
        super();
        add(new BoardState(boardRadius * 2 - 1, turn));
        showTurn = 0;
    }

    public boolean input(int x, int y){
        if( ! scoring ){
            return takeTurn(x, y);
        } else {
            if( scoreClick(x, y) ){
                updateCaptures();
                return true;
            } else return false;
        }
    }

    private boolean scoreClick(int x, int y){
        if( onBoardCheck(x, y)){
            Stack<TileState> changedTiles = currentTurn().markDead(x, y);
            if( changedTiles.isEmpty() ){
                return false;
            }
            calibrate();
            return true;
        }
        return false;
    }

    private boolean takeTurn(int x, int y){
        if( onBoardCheck(x, y)){
            int turn = currentTurn().getTurn();
            add(currentTurn().copy());
            Stack<TileState> changedTiles = currentTurn().takeTurn(x, y);
            if( changedTiles.isEmpty() ){
                undo();
                return false;
            }
            updateCaptures();
            coreIO.gameMessage("A" + x + ", B" + y, turn);
            updateTurnIO();
            coreIO.calibrateTiles(changedTiles);
            if( lastTurnWasPass ){ lastTurnWasPass = false; }
            showTurn = this.size()-1;
            return true;
        }
        return false;
    }

    public int getTurn(){
        return currentTurn().getTurn();
    }

    public int getCaptures( int turn ){
        return currentTurn().getCaptures(turn);
    }

    public void updateCaptures(){
        coreIO.setWhiteCaptures(getCaptures(TileState.WHITE));
        coreIO.setBlackCaptures(getCaptures(TileState.BLACK));
    }

    public BoardState currentTurn(){
        return this.get(this.size()-1);
    }

    public boolean onBoardCheck(int x, int y){
        return currentTurn().getTileState(x, y) != null;
    }

    public boolean isMarkedDead(int x, int y){
        if( onBoardCheck(x, y) ){
            return currentTurn().getTileState(x, y).isDead();
        }
        return false;
    }

    public boolean pass(){
        coreIO.gameMessage("Pass", currentTurn().getTurn());
        if( lastTurnWasPass ){
            scoring = true;
            coreIO.setScoring();
            add(currentTurn().copy());
            currentTurn().calculateAllScoreGroups();
            calibrate();
            showTurn = size()-1;
            return true;
        } else {
            lastTurnWasPass = true;
            add(currentTurn().copy());
            currentTurn().toggleTurn();
            updateTurnIO();
            showTurn = size()-1;
            return false;
        }
    }

    private void calibrate(){
        showTurn(this.size()-1);
    }

    public void showTurn( int turn ){
        try{
            BoardState turnState = get(turn);
            TileState[][] board = turnState.getBoard();
            Stack<TileState> allTiles = new Stack<TileState>();
            for( TileState[] row : board ){
                for( TileState tile : row ){
                    if( tile != null ){
                        allTiles.push(tile);
                    }
                }
            }
            coreIO.calibrateTiles(allTiles);
            coreIO.updateTurn(turnState.getTurn());
            coreIO.setBlackScore(turnState.getBlackScore());
            coreIO.setWhiteScore(turnState.getWhiteScore());
            coreIO.setBlackCaptures(turnState.getCaptures(TileState.BLACK));
            coreIO.setWhiteCaptures(turnState.getCaptures(TileState.WHITE));
            showTurn = turn;
        } catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public int getShownTurn(){ return showTurn; }

    public void undo(){
        if( size() > 1 ) {
            BoardState cur = this.pop();
            int size = cur.getDiameter();
            Stack<TileState> changestack = new Stack<TileState>();
            for( int i = 0; i < size; i++ ){
                for( int j = 0; j < size; j++){
                    if( cur.getStateOfTile(i, j) != currentTurn().getStateOfTile(i, j)){
                        changestack.push(currentTurn().getTileState(i, j));
                    }
                }
            }
            updateTurnIO();
            coreIO.calibrateTiles(changestack);
            if( scoring ){
                coreIO.setPlaying();
                scoring = false;
            }
        }
    }

    public boolean isScoring(){ return scoring; }


    public void setCoreIO( CoreIO coreIO ){ this.coreIO = coreIO; }

    private void updateTurnIO(){
        coreIO.updateTurn(getTurn());
    }


}
