package com.mygdx.hexago.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hexago.boardcore.BoardConfig;
import com.mygdx.hexago.boardcore.TileState;
import com.mygdx.hexago.handlers.GameStateManager;
import com.mygdx.hexago.network.*;

import java.io.IOException;


public class PlayServer extends PlayState{
    private Server server;
    private String clientName;

    public PlayServer( GameStateManager gsm ){
        super(gsm);
        this.server = gsm.getServer();


        server.addListener(new Listener(){
            public void received( Connection connection, Object object ){
                if( object instanceof Packet ){
                    if( object instanceof ClientConnectPacket){
                        ClientConnectPacket clientConnectPacket = (ClientConnectPacket)object;
                        clientConnectEvent(connection, clientConnectPacket);
                    } else if( object instanceof BoardPacket ){
                        BoardPacket boardPacket = (BoardPacket) object;
                        clientTakeTurn(boardPacket);
                    } else if( object instanceof ChatPacket ){
                        ChatPacket chatPacket = (ChatPacket) object;
                        receiveChatMessage(chatPacket);
                    } else if( object instanceof UndoPacket ){
                        UndoPacket undoPacket = (UndoPacket) object;
                        undoEvent(undoPacket);
                    } else if( object instanceof PassPacket ){
                        boardCore.pass();
                        System.out.println("pass received");
                    } else if( object instanceof MarkDeadPacket ){
                        MarkDeadPacket mdPack = (MarkDeadPacket) object;
                        if( boardCore.isMarkedDead(mdPack.x, mdPack.y) != mdPack.markdead){
                            boardActor.attemptPlay(mdPack.x, mdPack.y);
                        }
                    }
                }
            }
        });

        if( gsm.getBoardConfig().firstTurn == BoardConfig.CLIENT ){
            getBoardActor().setMyTurn(TileState.WHITE);
        } else {
            getBoardActor().setMyTurn(TileState.BLACK);
        }



    }

    public void undoEvent(UndoPacket undoPacket){
        if( undoPacket.request ){
            Dialog undoDialog = new Dialog("Opponent requests undo", skin);
            TextButton yesButton = new TextButton("Accept", skin);
            TextButton noButton = new TextButton("Refuse", skin);
            yesButton.addListener( new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                    UndoPacket replyPacket = new UndoPacket();
                    replyPacket.request = false;
                    replyPacket.accept = true;
                    server.sendToAllTCP(replyPacket);
                    boardCore.undo();
                    if( boardCore.getTurn() == boardActor.getMyTurn() ){
                        boardCore.undo();
                    }
                    unlock();
                }
            });
            noButton.addListener( new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                    UndoPacket replyPacket = new UndoPacket();
                    replyPacket.request = false;
                    replyPacket.accept = false;
                    server.sendToAllTCP(replyPacket);
                    unlock();
                }
            });
            undoDialog.button(yesButton);
            undoDialog.button(noButton);
            undoDialog.text("Opponent requests undo");
            lock();
            undoDialog.show(stage);
        } else {
            if( undoPacket.accept ){
                boardCore.undo();
                if( boardCore.getTurn() != boardActor.getMyTurn() ){
                    boardCore.undo();
                }
            }
            unlock();
        }
    }

    public void initialize(){
        chatField.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent e, int keyCode) {
                if( keyCode == Input.Keys.ENTER ){
                    String message = chatField.getText();
                    boardLog.playerMessage(message, boardActor.getMyTurn());
                    chatField.setText("");
                    ChatPacket chatPacket = new ChatPacket(message);
                    server.sendToAllTCP(chatPacket);
                }
                return false;
            }
        });
        InputListener undoListener = new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                UndoPacket undoPacket = new UndoPacket();
                undoPacket.request = true;
                undoPacket.accept = false;
                server.sendToAllTCP(undoPacket);
                lock();
            }
        };
        playUndoButton.addListener(undoListener);
        scoreUndoButton.addListener(undoListener);
        passButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                boardCore.pass();
                server.sendToAllTCP(new PassPacket());
            }
        });
    }

    public void turnEvent(int x, int y){
        if( boardCore.isScoring() ){
            MarkDeadPacket mdPack = new MarkDeadPacket();
            mdPack.x = x;
            mdPack.y = y;
            mdPack.markdead = boardCore.isMarkedDead( x, y );
            server.sendToAllTCP(mdPack);
        } else {
            if (!getBoardActor().isMyTurn()) {
                BoardPacket boardPacket = new BoardPacket();
                boardPacket.x = x;
                boardPacket.y = y;
                server.sendToAllTCP(boardPacket);
            }
        }
    }

    public void receiveChatMessage(ChatPacket chatPacket){
        boardLog.playerMessage(chatPacket.message, boardActor.getOppTurn());
    }

    private void clientTakeTurn(BoardPacket boardPacket){
        boardActor.attemptPlay(boardPacket.x, boardPacket.y);
    }

    private void clientConnectEvent(Connection connection, ClientConnectPacket clientConnectPacket){
        clientName = clientConnectPacket.clientName;
        if( gsm.getBoardConfig().firstTurn == BoardConfig.HOST ){
            gsm.getBoardConfig().whiteName = clientName;
        } else {
            gsm.getBoardConfig().blackName = clientName;
        }
        boardLog.setBlackName(gsm.getBoardConfig().blackName);
        boardLog.setWhiteName(gsm.getBoardConfig().whiteName);
        //gsm.getBoardConfig().opponentName = clientName;


        BoardConfigPacket boardConfigPacket = new BoardConfigPacket();
        boardConfigPacket.boardConfig = gsm.getBoardConfig();
        connection.sendTCP(boardConfigPacket);
    }

    public void dispose(){
        server.close();
        try{
            server.dispose();
        } catch( IOException e ){
            e.printStackTrace();
        }
    }
}
