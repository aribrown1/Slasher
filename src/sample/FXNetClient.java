package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import java.security.spec.ECField;
import java.util.HashMap;
import java.util.UUID;

public class FXNetClient extends Application{

    static boolean isServer = false;

    private NetworkConnectionClient conn;
    private TextArea messages = new TextArea();
    private int port;
    private String ip;
    private String clientID;
    boolean titlechanged = false;
    boolean assignedID = false;
    ImageView lizzardv, paperv, rockv, scissorsv, spockview, v, v2;
    Stage mystage = new Stage();
    Scene scene;
    Button on, portbutton, ipbutton;
    Label play1, play2, playerpoints;
    int portinput;
    String ipinput;
    TextField portfield, ipfield;
    Label inputinstrctions;
    HBox played;
    VBox client1played, client2played;
    Image spock, paper, rock, scissors, lizzard;
    Button playagain, quit;
    int firstplay;

    void createContent() {
        messages.setPrefHeight(250);
        TextField input = new TextField();

        input.setOnAction(event -> {
            String message = isServer ? "Server: " : "Client: ";
            message += input.getText();
            input.clear();


            messages.appendText(message + "\n");
            try {
                conn.send(message);
            }
            catch(Exception e) {

            }

        });


        lizzard = new Image("lizzard.jpg");
        lizzardv = new ImageView(lizzard);
        lizzardv.setFitHeight(115);
        lizzardv.setFitWidth(105);
        Button lizzardbtn = new Button();
        lizzardbtn.setGraphic(lizzardv);
        spock = new Image("spock.jpg");
        spockview = new ImageView(spock);
        spockview.setFitHeight(115);
        spockview.setFitWidth(105);
        Button spockbtn = new Button();
        spockbtn.setGraphic(spockview);
        paper = new Image("paper.jpg");
        paperv = new ImageView(paper);
        paperv.setFitHeight(115);
        paperv.setFitWidth(95);
        Button paperbtn = new Button();
        paperbtn.setGraphic(paperv);
        rock = new Image("rock.jpg");
        rockv = new ImageView(rock);
        rockv.setFitHeight(115);
        rockv.setFitWidth(110);
        Button rockbtn = new Button();
        rockbtn.setGraphic(rockv);
        scissors = new Image("scissors.jpg");
        scissorsv = new ImageView(scissors);
        scissorsv.setFitHeight(115);
        scissorsv.setFitWidth(105);
        Button scissorsbtn = new Button();
        scissorsbtn.setGraphic(scissorsv);
        lizzardbtn.setOnAction(choseLizzard);
        paperbtn.setOnAction(chosePaper);
        rockbtn.setOnAction(choseRock);
        scissorsbtn.setOnAction(choseScissors);
        spockbtn.setOnAction(choseSpock);

        final Pane spacer = new Pane(); /*first spacer*/
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(25, 0);
        final Pane spacer2 = new Pane(); /*second spacer*/
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        spacer2.setMinSize(20, 0);

        HBox container = new HBox();
        container.getChildren().setAll(spacer, spockbtn, lizzardbtn, paperbtn, rockbtn, scissorsbtn, spacer2);
        container.setPadding(new Insets(10, 10, 10, 10));

        HBox images= new HBox();
        images.getChildren().setAll(container);

        playerpoints = new Label("Player points: None");

        played = new HBox();
        play1 = new Label("Client 1 played");
        play2 = new Label("Client 2 played");
        client1played = new VBox();
        client2played = new VBox();
        client1played.getChildren().setAll(play1);
        client2played.getChildren().setAll(play2);
        played.getChildren().setAll(client1played, client2played);

        playagain = new Button("Play again");
        playagain.setOnAction(playAgain);
        quit = new Button("Quit game");
        quit.setOnAction(quitGame);
        HBox buttons = new HBox();
        buttons.getChildren().setAll(playagain, quit);


        VBox root = new VBox(20, messages, images,  input, buttons, playerpoints, played);
        root.setPrefSize(700, 700);
        Scene scene = new Scene(root);
        mystage.setScene(scene);
        mystage.show();

    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Welcome to the game!");

        Image pic = new Image("springbg.png");
        ImageView v1 = new ImageView(pic);

        on = new Button("Connect to server");
        on.setOnAction(startServer);

        portbutton = new Button("Submit port");
        portbutton.setOnAction(getPort);

        ipbutton = new Button("Submit IP");
        ipbutton.setOnAction(getIP);

        portfield = new TextField();
        ipfield = new TextField();
        inputinstrctions = new Label("Please type port and IP to connect to");
        HBox portinputstuff = new HBox(portfield, portbutton);
        HBox ipinputstuff = new HBox(ipfield, ipbutton);
        VBox inputstuff = new VBox(inputinstrctions, portinputstuff, ipinputstuff);

        Group smallGroup = new Group(inputstuff);

        StackPane stackPane = new StackPane();
        stackPane.setMinSize(800, 450);
        stackPane.setAlignment(smallGroup, Pos.CENTER);
        stackPane.setAlignment(on, Pos.TOP_LEFT);
        //stackPane.getChildren().addAll(v1, info, smallGroup);
        stackPane.getChildren().addAll(v1, on, smallGroup);

        scene = new Scene(stackPane, 800, 450);
        primaryStage.setScene(scene);

        primaryStage.show();
        mystage = primaryStage; //to use stage in other functions

    }

    public void startConn() throws Exception{
        conn.startConn();
    }

    @Override
    public void stop() throws Exception{
        conn.closeConn();
    }

    public void setPort(int p){
        this.port = p;
    }

    public void setIP(String ip){
        this.ip = ip;
    }

    public Client createClient() {

        return new Client(this.ip, this.port, data -> {
            Platform.runLater(()->{
                String tmp = data.toString();
                tmp = tmp.intern();
                String[] split = tmp.split(" ");
                if (split.length > 5){ //display what clients played
                    if (split[2].equals("played:")){
                        if (split[1].equals("1")) {
                            firstplay = 1;
                            updatePlayed(split[3], split[8], split[1]);
                        }else if (split[1].equals("2")){
                            updatePlayed(split[3], split[8], split[1]);
                            firstplay = 2;
                        }
                    }
                }

                if (split.length > 1) {
                    if (split[1].equals("points:")) { //to update round points[
                            updatePlayerPoints(split[4], split[7]); //check

                    }
                    if (split.length>2) {
                        if (split[2].equals("connected")) {
                            changeStageTitle(split[1]);
                        }
                    }
                }
                if (split[0].equals("Game")){
                    mystage.setTitle("Game over!");
                }
                if (split[0].equals("Playagain")){
                    clearPlayerPoints();
                }
                if (split.length == 3){ //assigned client in ID for FXNet
                    if (!assignedID) {
                        if (split[2].equals("joined!")) {
                            clientID = split[1];
                            changeStageTitle(split[1]);
                        }
                        System.out.println("assigning client id: " + clientID);
                        assignedID = true;
                    }
                }
                if (split.length > 3){
                    if (split[3].equals("disconnected.")){
                        clearPlayerPoints();
                    }
                }
                messages.appendText(data.toString() + "\n");

            });
        });

    }


    EventHandler<ActionEvent> choseLizzard = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            try {
                conn.send("lizzard " + conn.getID());

            }catch(Exception e){System.out.println("caught in server off"); }

        }
    };
    EventHandler<ActionEvent> chosePaper= new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            try {
                conn.send("paper " + conn.getID());
            }catch(Exception e){System.out.println("caught in server off"); }

        }
    };
    EventHandler<ActionEvent> choseRock= new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            try {
                conn.send("rock " + conn.getID());
            }catch(Exception e){System.out.println("caught in server off"); }

        }
    };
    EventHandler<ActionEvent> choseScissors = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            try {
                conn.send("scissors " + conn.getID());
            }catch(Exception e){System.out.println("caught in server off"); }

        }
    };
    EventHandler<ActionEvent> choseSpock = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            try {
                conn.send("spock " + conn.getID());
            }catch(Exception e){System.out.println("caught in server off"); }

        }
    };

    void changeStageTitle(String n){
        System.out.println("changing stage title");
        if (!titlechanged) {
            mystage.setTitle("Client " + n);
            titlechanged = true;
        }
    }

    EventHandler<ActionEvent> getPort = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            portinput =  Integer.parseInt(portfield.getText()); /*convert string to int*/
            setPort(portinput);

        }
    };

    EventHandler<ActionEvent> getIP = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            ipinput =  (ipfield.getText()); /*convert string to int*/
            setIP(ipinput);

        }
    };

    EventHandler<ActionEvent> startServer = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
           conn = createClient();
           // System.out.println("newnet.createclient");
            try {
                startConn();
                System.out.println("after startconn()");
                createContent();
                //mystage.show();
            }catch (Exception e){System.out.println("Catch in startClient event handler"); }

        }
    };

    void updatePlayed(String played1, String played2, String client){
        client1played.getChildren().clear();
        client2played.getChildren().clear();
        System.out.println("at top of updatePlayed");
        System.out.println("cleint1: " + play1);
        System.out.println("client2: " + play2);
        System.out.println("client " + client);


        if (played1.equals("lizzard")) {
            v = new ImageView("lizzard.jpg");
            v.setFitHeight(115);
            v.setFitWidth(95);
        }
        if (played1.equals("paper")) {
            v = new ImageView("paper.jpg");
            v.setFitHeight(115);
            v.setFitWidth(95);
        }
        if (played1.equals("rock")) {
            v = new ImageView("rock.jpg");
            v.setFitHeight(115);
            v.setFitWidth(95);
        }
        if (played1.equals("scissors")) {
            v = new ImageView("scissors.jpg");
            v.setFitHeight(115);
            v.setFitWidth(95);
        }
        if (played1.equals("spock")) {
            v = new ImageView("spock.jpg");
            v.setFitHeight(115);
            v.setFitWidth(95);
        }

        if (played2.equals("lizzard")) {
            v2 = new ImageView("lizzard.jpg");
            v2.setFitHeight(115);
            v2.setFitWidth(95);
        }
        if (played2.equals("paper")) {
            v2 = new ImageView("paper.jpg");
            v2.setFitHeight(115);
            v2.setFitWidth(95);
        }
        if (played2.equals("rock")) {
            v2 = new ImageView("rock.jpg");
            v2.setFitHeight(115);
            v2.setFitWidth(95);
        }
        if (played2.equals("scissors")) {
            v2 = new ImageView("scissors.jpg");
            v2.setFitHeight(115);
            v2.setFitWidth(95);
        }
        if (played2.equals("spock")) {
            v2 = new ImageView("spock.jpg");
            v2.setFitHeight(115);
            v2.setFitWidth(95);
        }

        if (client.equals("2")){
            client1played.getChildren().setAll(v2, play1);
            client2played.getChildren().setAll(v, play2);
        }else if (client.equals("1")) {
            client1played.getChildren().setAll(v, play1);
            client2played.getChildren().setAll(v2, play2);
        }



        final Pane spacer = new Pane();
        spacer.setMinSize(50, 0);
        played.getChildren().clear();
        played.getChildren().setAll(client1played, spacer, client2played);
        played.setAlignment(Pos.CENTER);
        mystage.show();

    }

    public void updatePlayerPoints(String play1pnt, String play2pnt){

        System.out.println("In update played points");
        playerpoints.setText("");
        if (firstplay == 1) {
            playerpoints.setText("Player points: Player 1: " + play1pnt + " Player 2: " + play2pnt);
        }
        else if (firstplay == 2){
            playerpoints.setText("Player points: Player 1: " + play2pnt + " Player 2: " + play1pnt);
        }
        mystage.show();

    }

    EventHandler<ActionEvent> playAgain = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            try {
                conn.send("Playagain");
            }catch(Exception e){System.out.println("Caught in play again"); }
        }
    };

    EventHandler<ActionEvent> quitGame = new EventHandler<ActionEvent>(){

        public void handle(ActionEvent event) {
            try{
                String quit = "Quit: " + clientID;
                System.out.println(quit);
                conn.send(quit);
                conn.closeConn();
                stop();
            }catch(Exception e){ System.out.println("Caught in quit game"); }
        }
    };

    void clearPlayerPoints(){
        playerpoints.setText("");
        playerpoints.setText("Player points: None");
        mystage.show();
    }

    boolean returnTitlechanged(){
        return this.titlechanged;
    }

    boolean returnAssignedID(){
        return this.assignedID;
    }


}