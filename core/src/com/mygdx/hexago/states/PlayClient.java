package com.mygdx.hexago.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.hexago.boardcore.BoardConfig;
import com.mygdx.hexago.boardcore.TileState;
import com.mygdx.hexago.handlers.GameStateManager;
import com.mygdx.hexago.network.*;


public class PlayClient extends PlayState{
    private Client client;


    public PlayClient(GameStateManager gsm){
        super(gsm);
        this.client = gsm.getClient();
        client.addListener(new Listener(){
            public void received( Connection connection, Object object ){
                if( object instanceof Packet){
                    if( object instanceof BoardPacket ){
                        BoardPacket boardPacket = (BoardPacket) object;
                        serverTakeTurn(boardPacket);
                    } else if( object instanceof ChatPacket ){
                        ChatPacket chatPacket = (ChatPacket) object;
                        receiveChatMessage(chatPacket);
                    } else if( object instanceof UndoPacket){
                        UndoPacket undoPacket = (UndoPacket) object;
                        undoEvent(undoPacket);
                    } else if( object instanceof PassPacket){
                        System.out.println("pass received");
                        boardCore.pass();
                    }
                }
            }
        });
        getBoardActor().setMyTurn(TileState.BLACK);
        if( gsm.getBoardConfig().firstTurn == BoardConfig.CLIENT ){
            getBoardActor().setMyTurn(TileState.BLACK);
        } else {
            getBoardActor().setMyTurn(TileState.WHITE);
        }
        boardLog.setBlackName(gsm.getBoardConfig().blackName);
        boardLog.setWhiteName(gsm.getBoardConfig().whiteName);
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
                    client.sendTCP(chatPacket);
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
                client.sendTCP(undoPacket);
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
                PassPacket passPacket = new PassPacket();
                client.sendTCP(passPacket);
            }
        });
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
                    client.sendTCP(replyPacket);
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
                    client.sendTCP(replyPacket);
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

    public void receiveChatMessage(ChatPacket chatPacket){
        boardLog.playerMessage(chatPacket.message, boardActor.getOppTurn());
    }

    public void turnEvent(int x, int y){
        if( ! getBoardActor().isMyTurn() ){
            BoardPacket boardPacket = new BoardPacket();
            boardPacket.x = x;
            boardPacket.y = y;
            client.sendTCP(boardPacket);
        }
    }

    private void serverTakeTurn(BoardPacket boardPacket){
        getBoardActor().attemptPlay(boardPacket.x, boardPacket.y);
    }

}
