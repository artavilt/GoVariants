package com.mygdx.hexago.boardcore;

import com.mygdx.hexago.ui.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class BoardState {
    private static final int[][] dirVectors = { {1, 0},  {-1, 0},
                                                {0, 1},  {0, -1},
                                                {1, -1}, {-1, 1} };

    private TileState[][] board;
    private int turn;
    private Integer groupcounter;
    private Integer scoreGroupCounter;

    private HashMap<Integer, ArrayList<TileState>> groups;
    private HashMap<Integer, ArrayList<TileState>> scoregroups;

    private Integer whiteCaptures;
    private Integer blackCaptures;
    private Integer whiteScore;
    private Integer blackScore;





    //should probably allow for other shapes.
    public BoardState(int diameter, int turn){
        board = new TileState[diameter][diameter];
        for(int i = 0; i < diameter; i++){
            for(int j = 0; j < diameter; j++){
                board[i][j] = new TileState(i, j, TileState.BASE);
            }
        }
        // setting the phantom tiles to null
        for( int diag = 0; diag < (diameter-1)/2; diag++){
            for( int r = 0; r <= diag; r++ ){
                board[diag - r][ r ] = null;
                board[diameter - 1 - diag + r][diameter - 1 - r] = null;
            }
        }
        groupcounter = 0;
        whiteCaptures = 0;
        blackCaptures = 0;
        whiteScore = 0;
        blackScore = 0;
        this.turn = turn;
        groups = new HashMap<Integer, ArrayList<TileState>>();
        scoregroups = new HashMap<Integer, ArrayList<TileState>>();
    }

    public boolean isValidMove( TileState tile ){
        boolean answer = false;
        if( tile == null ){
            return false;
        } else {
            for( int[] dir : dirVectors){
                try{
                   if( getTileState( tile.x + dir[0], tile.y + dir[1] ).state != getNextTurn() ){
                       answer = true;
                   }
                } catch( NullPointerException e ){}
            }
        }

        return answer;
    }

    public Stack<TileState> takeTurn(int x, int y){
        Stack<TileState> changedTiles = new Stack<TileState>();
        if( placeStone(x, y, changedTiles) ){
            System.out.println( "Player " + this.turn + ": "
                    + "A" + x + ", B" + y);
            toggleTurn();
            return changedTiles;
        } else return new Stack<TileState>();
    }

    public Stack<TileState> markDead(int x, int y){
        TileState stone = getTileState(x, y);
        Stack<TileState> changedTiles = new Stack<TileState>();
        if( stone == null ) return changedTiles;
        if( stone.state == TileState.BASE ){ return changedTiles; }
        ArrayList<TileState> stonegroup = groups.get(stone.group);
        int dir = stone.isDead() ? -1 : 1;
        if( stonegroup != null){
            for( TileState t : stonegroup ){
                t.state = t.toggleDeadState(t.state);
                changedTiles.push( t );
                if( t.isWhite() ){
                    blackCaptures += dir;
                } else {
                    whiteCaptures += dir;
                }
            }
        }
        calculateAllScoreGroups();
        return changedTiles;
    }



    public void toggleTurn(){ this.turn = this.turn == TileState.WHITE ? TileState.BLACK : TileState.WHITE; }

    public int getTurn(){
        return this.turn;
    }

    public int getNextTurn(){ return this.turn == TileState.WHITE ? TileState.BLACK : TileState.WHITE; }

    public int getCaptures( int color ){
        return color == TileState.WHITE ? whiteCaptures : blackCaptures;
    }



    public TileState getTileState(int x, int y){
        try{
            return board[x][y];
        } catch( ArrayIndexOutOfBoundsException e){
            return null;
        }

    }

    public int getDiameter(){
        return board.length;
    }

    public int getStateOfTile( int x, int y){
        try{ return board[x][y].state; }
        catch( ArrayIndexOutOfBoundsException e ){ return TileState.NULL; }
        catch( NullPointerException e){ return TileState.NULL; }

    }

    public TileState[][] getBoard(){ return board; }

    public int getWhiteScore(){ return whiteScore; }

    public int getBlackScore(){ return blackScore; }

    public BoardState copy(){
        BoardState b = new BoardState(board.length, this.turn);
        for(int i = 0; i < board.length; i++ ){
            for(int j = 0; j < board[i].length; j++){
                TileState t = b.getTileState(i, j);
                if( t != null ){
                    t.state = board[i][j].state;
                    t.group = board[i][j].group;
                }
            }
        }
        for( Integer i : groups.keySet() ){
            ArrayList<TileState> gCopy = new ArrayList<TileState>();
            for( TileState tile : groups.get(i) ){
                gCopy.add( b.getTileState(tile.x, tile.y) );
                b.getTileState(tile.x, tile.y).group = i;
            }
            b.addGroup(i, gCopy);
        }
        for( Integer i : scoregroups.keySet() ){
            ArrayList<TileState> gCopy = new ArrayList<TileState>();
            for( TileState tile : scoregroups.get(i) ){
                gCopy.add( b.getTileState(tile.x, tile.y) );
                b.getTileState(tile.x, tile.y).scoregroup = i;
            }
            b.addScoreGroup(i, gCopy);
        }
        b.groupcounter = groupcounter;
        b.whiteCaptures = whiteCaptures;
        b.blackCaptures = blackCaptures;
        return b;
    }

    private void addScoreGroup(Integer index, ArrayList<TileState> scoreGroup ){
        scoregroups.put(index, scoreGroup);
    }

    private void addGroup( Integer index, ArrayList<TileState> group ){
        groups.put(index, group);
    }

    /** tries to place a stone in at x, y. If the move isn't valid, the stone isn't
     placed and false is returned. Otherwise the stone is placed, any other changes
     are calculated, and true is returned. */
    private boolean placeStone(int x, int y, Stack<TileState> changedTiles){
        Integer captures = 0;
        TileState placeTile = getTileState(x, y);
        if( placeTile.state == TileState.BASE ){
            placeTile.state = this.turn;
        } else return false;
        Stack<TileState> deadTiles = calculateKills(placeTile);
        for( TileState tile: deadTiles ){
            captures++;
            tile.state = TileState.BASE;
            tile.group = 0;
        }
        changedTiles.addAll(deadTiles);
        if( ( ! isValidMove( placeTile ) ) && changedTiles.isEmpty()){
            placeTile.state = TileState.BASE;
            return false;
        }
        changedTiles.push(placeTile);
        calculateGroups(placeTile);
        if( ! lifeCheck( placeTile ) ){
            placeTile.state = TileState.BASE;
            return false;
        }
        if( this.turn == TileState.WHITE ){ whiteCaptures += captures; }
        else{ blackCaptures += captures; }
        return true;
    }

    /** Gathers all groups adjacent to placeTile into a new group containing placeTile. */
    private void calculateGroups( TileState placeTile ){
        ArrayList<TileState> newgroup = new ArrayList<TileState>();
        newgroup.add(placeTile);
        groupcounter++;
        placeTile.group = groupcounter;
        for( int[] d : dirVectors ){
            TileState adjacentTile = getTileState( placeTile.x + d[0], placeTile.y + d[1] );
            if( adjacentTile != null ) {
                if (adjacentTile.state == this.turn && !( adjacentTile.group.equals( placeTile.group ) ) ){
                    ArrayList<TileState> oldGroup = groups.get(adjacentTile.group);
                    if (oldGroup != null) {
                        for (TileState t : oldGroup) {
                            t.group = placeTile.group;
                            newgroup.add(t);
                        }
                    }
                    groups.remove(adjacentTile.group);
                }
            }
        }
        groups.put(placeTile.group, newgroup);
    }

    public void calculateAllScoreGroups(){
        scoregroups = new HashMap<Integer, ArrayList<TileState>>();
        scoreGroupCounter = 1;
        whiteScore = 0;
        blackScore = 0;
        for( TileState[] row : board ){
            for( TileState tile : row ){
                if( tile != null ){
                    tile.scoregroup = 0;
                }
            }
        }
        for( TileState[] row : board ){
            for( TileState tile : row ){
                if( tile != null ){
                    if( tile.scoregroup == 0 &&
                            (tile.state != TileState.BLACK && tile.state != TileState.WHITE) ){
                        tile.scoregroup = scoreGroupCounter;
                        scoreGroupCounter++;
                        ArrayList<TileState> scoreGroup = new ArrayList<TileState>();
                        scoreGroup.add(tile);
                        calculateScoreGroup(tile, scoreGroup);
                        scoregroups.put(scoreGroupCounter, scoreGroup);
                    }
                }
            }
        }
        assignScoreGroups();
        whiteScore += whiteCaptures;
        blackScore += blackCaptures;
    }

    private void calculateScoreGroup( TileState tile, ArrayList<TileState> scoreGroup ){
        for( int[] dir : dirVectors ){
            if( tile != null ){
                TileState adjacentTile = getTileState( tile.x + dir[0], tile.y + dir[1]);
                if( adjacentTile.state != TileState.BLACK
                        && adjacentTile.state != TileState.WHITE
                        && adjacentTile.scoregroup == 0){
                        adjacentTile.scoregroup = tile.scoregroup;
                        //adjacentTile.state = TileState.WHITESCORE;
                        scoreGroup.add(adjacentTile);
                        calculateScoreGroup(adjacentTile, scoreGroup);
                }
            }
        }
    }

    private void assignScoreGroups(){
        for( Integer i : scoregroups.keySet() ){
            ArrayList<TileState> scoreGroup = scoregroups.get(i);
            boolean blackBorder = false;
            boolean whiteBorder = false;
            for( TileState tile : scoreGroup){
                for( int[] dir : dirVectors ){
                    TileState adjacentTile = getTileState(tile.x + dir[0], tile.y + dir[1]);
                    if( adjacentTile != null ){
                        if (adjacentTile.state == TileState.WHITE) {
                            whiteBorder = true;
                        } else if (adjacentTile.state == TileState.BLACK) {
                            blackBorder = true;
                        }
                    }
                }
            }
            if( whiteBorder && blackBorder ){
                for( TileState tile : scoreGroup ){
                    if( tile.state == TileState.WHITESCORE || tile.state == TileState.BLACKSCORE ){
                        tile.state = TileState.BASE;
                    }
                }
            } else if( whiteBorder ){
                for( TileState tile : scoreGroup ){
                    whiteScore++;
                    if( tile.state == TileState.BASE ){
                        tile.state = TileState.WHITESCORE;
                    }
                }
            } else if( blackBorder ){
                for( TileState tile : scoreGroup ){
                    blackScore++;
                    if( tile.state == TileState.BASE ){
                        tile.state = TileState.BLACKSCORE;
                    }
                }
            }
        }
    }

    /** Checks if the group containing placeTile is alive. */
    private boolean lifeCheck( TileState placeTile ){
        ArrayList<TileState> oppGroup = groups.get(placeTile.group);
        for( TileState tile : oppGroup ){
            for( int[] dir : dirVectors ){
                TileState adjacentTile = getTileState(tile.x + dir[0], tile.y + dir[1]);
                if( adjacentTile != null ) {
                    if (adjacentTile.state == TileState.BASE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Checks if adjacent opposite groups to placeTile are dead. Returns a stack
        of all the dead tiles. */
    private Stack<TileState> calculateKills( TileState placeTile ){
        Stack<TileState> deadTiles = new Stack<TileState>();
        ArrayList<Integer> adjacentGroups = new ArrayList<Integer>();
        for( int[] d : dirVectors ) {
            TileState adjacentTile = getTileState( placeTile.x + d[0], placeTile.y + d[1] );
            if( adjacentTile != null ){
                int state = adjacentTile.state;
                if ( state == getNextTurn() && ! lifeCheck( adjacentTile ) &&
                        ! adjacentGroups.contains(adjacentTile.group) ) {
                    adjacentGroups.add(adjacentTile.group);
                    ArrayList<TileState> oppGroup = groups.get(adjacentTile.group);
                    for( TileState tile : oppGroup ){
                        deadTiles.push(tile);
                    }
                }
            }
        }
        return deadTiles;
    }
}
