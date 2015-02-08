package com.mygdx.hexago.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.hexago.handlers.GameStateManager;
import com.mygdx.hexago.network.*;

import java.io.IOException;

public class ClientConnect extends MenuState {
    private Client client;
    private NetConfig netConfig;
    private Label connectingLabel;
    private TextField userNameTextField;
    private TextField tcpTextField;
    private TextField udpTextField;
    private TextField ipTextField;
    private boolean connectionComplete = false;

    public ClientConnect(GameStateManager gsm){
        super(gsm);


        netConfig = new NetConfig();

        this.client = gsm.getClient();


        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof BoardConfigPacket) {
                    BoardConfigPacket config = (BoardConfigPacket)object;
                    startGame(config);
                }
            }
        });


        // CONNECTING STATUS LABEL
        connectingLabel = new Label("Waiting to connect", skin);
        connectingLabel.setAlignment(Align.center);
        connectingLabel.setPosition(450, 350);


        // EXIT TO MENU
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setPosition(600, 200);
        exitButton.setSize(140, 40);

        // CONNECT
        TextButton connectButton = new TextButton("Connect", skin);
        connectButton.setPosition(300, 200);
        connectButton.setSize(140, 40);

        // USER NAME
        Label userNameLabel = new Label("Enter User Name", skin);
        userNameLabel.setAlignment(Align.right);
        userNameLabel.setPosition(340, 250);
        userNameLabel.setSize(100, 30);
        userNameTextField = new TextField("", skin);
        userNameTextField.setMessageText("Hank");
        userNameTextField.setAlignment(Align.center);
        userNameTextField.setPosition(450, 250);
        userNameTextField.setSize(100, 30);

        // Set IP Address
        Label ipLabel = new Label("Enter host IP", skin);
        ipLabel.setAlignment(Align.right);
        ipLabel.setPosition(340, 300);
        ipLabel.setSize(100, 30);
        ipTextField = new TextField("", skin);
        ipTextField.setMessageText(netConfig.IPAdress);
        ipTextField.setAlignment(Align.center);
        ipTextField.setPosition(450, 300);
        ipTextField.setSize(100, 30);

        // Set TCP Port
        Label tcpLabel = new Label("TCP port", skin);
        tcpLabel.setAlignment(Align.right);
        tcpLabel.setPosition(580, 300);
        tcpLabel.setSize(60, 30);
        tcpTextField = new TextField("", skin);
        tcpTextField.setText(Integer.toString(netConfig.TCPPort));
        tcpTextField.setAlignment(Align.center);
        tcpTextField.setPosition(650, 300);
        tcpTextField.setSize(60, 30);

        // Set UDP Port
        Label udpLabel = new Label("UDP Port", skin);
        udpLabel.setAlignment(Align.right);
        udpLabel.setPosition(580, 250);
        udpLabel.setSize(60, 30);
        udpTextField = new TextField("", skin);
        udpTextField.setText(Integer.toString(netConfig.UDPPort));
        udpTextField.setAlignment(Align.center);
        udpTextField.setPosition(650, 250);
        udpTextField.setSize(60, 30);


        stage.addActor(connectingLabel);
        stage.addActor( udpLabel );
        stage.addActor( ipLabel );
        stage.addActor( tcpLabel );
        stage.addActor( userNameLabel );
        stage.addActor( ipTextField);
        stage.addActor( userNameTextField );
        stage.addActor( udpTextField );
        stage.addActor( tcpTextField );
        stage.addActor( exitButton );
        stage.addActor(connectButton);

        exitButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                exitToMenu();
            }
        });
        connectButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                connect();
            }
        });

        udpTextField.setTextFieldFilter(new TextField.TextFieldFilter() {
            public boolean acceptChar(TextField textField, char c) {
                return portTextCheck(textField.getText(), c);
            }
        });
        tcpTextField.setTextFieldFilter(new TextField.TextFieldFilter() {
            public boolean acceptChar(TextField textField, char c){return portTextCheck(textField.getText(), c);
            }
        });

        ipTextField.setTextFieldFilter(new TextField.TextFieldFilter() {
            public boolean acceptChar(TextField textField, char c) {
                String text = textField.getText();
                char dot = ".".charAt(0);
                if (text.length() > 15) {
                    return false;
                } else if (c == dot) {
                    if (text.length() == 0) {
                        return false;
                    } else if (text.charAt(text.length() - 1) == dot) {
                        return false;
                    }
                    return true;
                } else if (!Character.isDigit(c)) {
                    return false;
                }
                return true;
            }
        });
    }

    private boolean portTextCheck(String field, char c){
        if(  ! Character.isDigit(c) ){
            return false;
        } else if( Integer.parseInt(field + c) > 65535 ){
            return false;
        }
        return true;
    }

    private void connect(){
        netConfig.IPAdress = ipTextField.getText();
        netConfig.TCPPort = Integer.parseInt(tcpTextField.getText());
        netConfig.UDPPort = Integer.parseInt(udpTextField.getText());

        connectingLabel.setText("Connecting to server...");
        client.start();

        try{
            client.connect(10000, netConfig.IPAdress, netConfig.TCPPort, netConfig.UDPPort);
            connectingLabel.setText("Connected to server!");
            ClientConnectPacket clientConnectPacket = new ClientConnectPacket();
            clientConnectPacket.clientName = userNameTextField.getText();
            client.sendTCP(clientConnectPacket);

        } catch ( IOException e ){
            e.printStackTrace();
            connectingLabel.setText("Could not connect to server!");
        }

    }

    private void startGame(BoardConfigPacket config){
        gsm.setBoardConfig(config.boardConfig);
        connectionComplete = true;
    }

    private void exitToMenu(){
        client.close();
        gsm.setState(GameStateManager.GUIMENU);
    }

    public void update(float dt){}
    // called when the state is returned to the top of the stack.
    public void focus(){}
    //public abstract void handleInput();
    public void render(){
        if( connectionComplete ){ gsm.setState(GameStateManager.CLIENT); }
        Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        stage.act();
        stage.draw(); }

    public void dispose(){
        //stage.dispose();
        //skin.dispose();
    }
}
