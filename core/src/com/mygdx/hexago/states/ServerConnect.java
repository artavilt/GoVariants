package com.mygdx.hexago.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hexago.boardcore.BoardConfig;
import com.mygdx.hexago.handlers.GameStateManager;
import com.mygdx.hexago.network.NetConfig;

import java.io.IOException;


public class ServerConnect extends GameState {
    private Stage stage;
    private Server server;
    NetConfig netConfig;
    Skin skin;
    private TextField tcpTextField;
    private TextField udpTextField;
    private SelectBox<Integer> boardRadiusSelect;
    private TextField userNameTextField;


    public ServerConnect(GameStateManager gsm){
        super(gsm);
        this.server = gsm.getServer();
        netConfig = new NetConfig();


        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));


        // Set TCP Port
        Label tcpLabel = new Label("TCP port", skin);
        tcpLabel.setAlignment(Align.right);
        tcpLabel.setPosition(580, 300);
        //tcpLabel.setSize(60, 30);
        tcpTextField = new TextField("", skin);
        tcpTextField.setText(Integer.toString(netConfig.TCPPort));
        tcpTextField.setAlignment(Align.center);
        tcpTextField.setPosition(650, 300);
        tcpTextField.setSize(60, 30);

        // Set UDP Port
        Label udpLabel = new Label("UDP Port", skin);
        udpLabel.setAlignment(Align.right);
        udpLabel.setPosition(580, 250);
        //udpLabel.setSize(60, 30);
        udpTextField = new TextField("", skin);
        udpTextField.setText(Integer.toString(netConfig.UDPPort));
        udpTextField.setAlignment(Align.center);
        udpTextField.setPosition(650, 250);
        udpTextField.setSize(60, 30);

        // BoardActor Radius Select
        Label boardRadiusLabel = new Label("Set BoardActor Radius", skin);
        boardRadiusLabel.setPosition(300, 300);
        boardRadiusLabel.setAlignment(Align.right);
        //boardRadiusLabel.setSize(100, 30);
        boardRadiusSelect = new SelectBox<Integer>(skin);
        boardRadiusSelect.setItems(new Array<Integer>(BoardConfig.RADIUSCHOICES));
        boardRadiusSelect.setSelected(gsm.getBoardConfig().boardRadius);
        boardRadiusSelect.setPosition(410, 300);
        boardRadiusSelect.setSize(50, 30);

        // USER NAME
        Label userNameLabel = new Label("Enter User Name", skin);
        userNameLabel.setAlignment(Align.right);
        userNameLabel.setPosition(340, 250);
        userNameLabel.setSize(100, 30);
        userNameTextField = new TextField("", skin);
        userNameTextField.setText("Hank");
        userNameTextField.setAlignment(Align.center);
        userNameTextField.setPosition(450, 250);
        userNameTextField.setSize(100, 30);


        // Host
        TextButton hostButton = new TextButton("Host", skin);
        hostButton.setPosition(300, 200);
        hostButton.setSize(140, 40);

        // EXIT TO MENU
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setPosition(600, 200);
        exitButton.setSize(140, 40);


        stage.addActor( exitButton );
        stage.addActor( hostButton );
        stage.addActor( udpLabel );
        stage.addActor( udpTextField );
        stage.addActor( tcpLabel );
        stage.addActor( tcpTextField );
        stage.addActor( boardRadiusLabel );
        stage.addActor( boardRadiusSelect );
        stage.addActor( userNameLabel );
        stage.addActor( userNameTextField );

        udpTextField.setTextFieldFilter(new TextField.TextFieldFilter() {
            public boolean acceptChar(TextField textField, char c){return portTextCheck(textField.getText(), c);
            }
        });
        tcpTextField.setTextFieldFilter(new TextField.TextFieldFilter() {
            public boolean acceptChar(TextField textField, char c){return portTextCheck(textField.getText(), c);
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                exitToMenu();
            }
        });
        hostButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                startServer();
            }
        });
        boardRadiusSelect.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                setBoardRadius();
            }
        });

    }

    private void setBoardRadius(){
        gsm.getBoardConfig().setRadius(boardRadiusSelect.getSelected());
    }

    private boolean portTextCheck(String field, char c){
        if(  ! Character.isDigit(c) ){
            return false;
        } else if( Integer.parseInt(field + c) > 65535 ){
            return false;
        }
        return true;
    }

    private void startServer(){
        netConfig.TCPPort = Integer.parseInt(tcpTextField.getText());
        netConfig.UDPPort = Integer.parseInt(udpTextField.getText());
        try{
            server.start();
            server.bind(netConfig.TCPPort, netConfig.UDPPort);
        } catch ( IOException e ){
            e.printStackTrace();
        }
        if( gsm.getBoardConfig().firstTurn == BoardConfig.HOST ){
            gsm.getBoardConfig().blackName = userNameTextField.getText();
        } else {
            gsm.getBoardConfig().whiteName = userNameTextField.getText();
        }
        gsm.setState( GameStateManager.HOST );
    }

    private void exitToMenu(){
        server.close();
        gsm.setState(GameStateManager.GUIMENU);
    }

    public void update(float dt){}
    // called when the state is returned to the top of the stack.
    public void focus(){}
    //public abstract void handleInput();
    public void resize(int width, int height){}

    public void render(){
        Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        stage.act();
        stage.draw(); }
    public void dispose(){
        stage.dispose();
        skin.dispose();
    }
}
