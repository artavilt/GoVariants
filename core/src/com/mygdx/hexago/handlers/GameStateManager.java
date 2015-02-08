package com.mygdx.hexago.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hexago.boardcore.BoardConfig;
import com.mygdx.hexago.main.Game;
import com.mygdx.hexago.network.*;
import com.mygdx.hexago.states.*;

import java.util.Stack;

public class GameStateManager {

    private Game game;

    private Stack<GameState> gameStates;

    public static final int PLAY = 0;
    public static final int GUIMENU = 2;
    public static final int OPTIONSMENU = 3;
    public static final int CLIENT = 4;
    public static final int HOST = 5;
    public static final int CLIENTCONNECT = 6;
    public static final int SERVERCONNECT = 7;

    private Server server;
    private Client client;
    private BoardConfig boardConfig;
    private Skin skin;

    public GameStateManager(Game game){
        this.game = game;
        gameStates = new Stack<GameState>();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        pushState(GUIMENU);
        boardConfig = new BoardConfig();
        server = new Server();
        client = new Client();
        client.getKryo().register( Packet.class );
        client.getKryo().register( ClientConnectPacket.class );
        client.getKryo().register( BoardConfigPacket.class );
        client.getKryo().register( BoardConfig.class );
        client.getKryo().register( BoardPacket.class );
        client.getKryo().register( ChatPacket.class );
        client.getKryo().register( UndoPacket.class );
        client.getKryo().register( PassPacket.class );
        client.getKryo().register( MarkDeadPacket.class );
        server.getKryo().register( Packet.class );
        server.getKryo().register( ClientConnectPacket.class );
        server.getKryo().register( BoardConfigPacket.class );
        server.getKryo().register( BoardConfig.class );
        server.getKryo().register( BoardPacket.class);
        server.getKryo().register( ChatPacket.class );
        server.getKryo().register( UndoPacket.class );
        server.getKryo().register( PassPacket.class );
        server.getKryo().register( MarkDeadPacket.class );
    }

    public Game game() { return game; }

    public Server getServer() { return server; }
    public Client getClient() { return client; }
    public BoardConfig getBoardConfig() { return boardConfig; }

    public Skin getSkin(){ return skin; }

    private GameState getState(int state){
        if( state == PLAY ){
            Play play = new Play(this);
            play.initialize();
            return play; }
        else if( state == GUIMENU )         { return new GUIMenu(this); }
        else if( state == OPTIONSMENU )     { return new OptionsMenu(this, boardConfig); }
        else if( state == CLIENTCONNECT)    { return new ClientConnect(this); }
        else if( state == SERVERCONNECT)    { return new ServerConnect(this); }
        else if( state == HOST )            {
            PlayServer playServer = new PlayServer(this);
            playServer.initialize();
            return playServer; }
        else if( state == CLIENT )          {
            PlayClient playClient = new PlayClient(this);
            playClient.initialize();
            return playClient; }
        return null;
    }

    public void setState(int state){
        popState();
        pushState(state);
    }

    public void pushState(int state){
        gameStates.push(getState(state));
    }

    public void resize( int width, int height ){
        gameStates.peek().resize(width, height);
    }

    public void popState(){
        GameState g = gameStates.pop();
        g.dispose();
        if( ! gameStates.isEmpty() ){
            gameStates.peek().focus();
        }
    }

    public void setBoardConfig(BoardConfig boardConfig){
        this.boardConfig = boardConfig;
    }

    public void update(float dt){
        gameStates.peek().update(dt);
    }

    public void render(){
        gameStates.peek().render();
    }

    public String toString(){
        return gameStates.toString();
    }
}
