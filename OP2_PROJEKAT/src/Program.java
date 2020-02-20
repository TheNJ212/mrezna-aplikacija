import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;


public class Program extends Application {

    public static final int TCP_PORT = 9000;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        //postavka prozora
        primaryStage.setTitle("OP2 PROJEKAT");
        primaryStage.setHeight(600.0);
        primaryStage.setWidth(800.0);
        primaryStage.setX(140.0);
        primaryStage.setY(100.0);

        //1. zadatak
        //Labela palindrom
        Label lblPalindrom = new Label("Da li je palindrom:");
        lblPalindrom.setMinWidth(100);
        lblPalindrom.setAlignment(Pos.BOTTOM_RIGHT);

        //Polje za unos palindroma
        TextField txtPalindrom = new TextField();
        txtPalindrom.setMinWidth(400);
        txtPalindrom.setMaxWidth(400);

        //Dugme za proveru palindroma
        Button btnPalindrom = new Button("Proveri");
        btnPalindrom.setOnAction(event -> {
            if(!txtPalindrom.getText().isEmpty()) {
            String response=komunikacija("palindrom###"+txtPalindrom.getText());
            MessageBox.show(response,"Palindrom");
            } else MessageBox.show("Unesite izraz","Palindrom");

        });


        //HBox za prvi zadatak
        HBox panePalindrom = new HBox(20, lblPalindrom, txtPalindrom, btnPalindrom);
        panePalindrom.setPadding(new Insets(10));


        //2. zadatak
        //prikaz fajlova sa servera
        ObservableList<String> datoteke = FXCollections.observableArrayList();
        ListView<String> lvprikaz=new ListView<>(datoteke);
        lvprikaz.setMaxWidth(400);
        lvprikaz.setMinWidth(400);
        lvprikaz.setMaxHeight(260);

        //pribavljanje podataka o fajlovima na serveru
        String prihvatFajlova=komunikacija("files###check");
        String[] fajlovi={};

        if(!prihvatFajlova.equals("Nema fajlova za prikaz")){
            fajlovi=prihvatFajlova.split("###");
            for (int i=0;i<fajlovi.length;i++){
                datoteke.add(fajlovi[i].split(":")[0]);
            }

        }else MessageBox.show(prihvatFajlova,"Greska!");

        //dugme za preuzimanje fajla
        Button btnPreuzmi=new Button("Preuzmi fajl");
        String[] finalFajlovi = fajlovi;
        btnPreuzmi.setOnAction(event -> {
            if(!lvprikaz.getSelectionModel().isEmpty()){
                String fajl=lvprikaz.getSelectionModel().getSelectedItem();
                //mesto gde ce se smestiti novi fajl
                DirectoryChooser directoryChooser=new DirectoryChooser();
                File desktop = new File(System.getProperty("user.home") + File.separator + "Desktop");
                directoryChooser.setInitialDirectory(desktop);//inicijalni folder koji se otvara, desktop
                directoryChooser.setTitle("Izaberite lokaciju za preuzimanje");
                File smestiU= directoryChooser.showDialog(primaryStage);
                if(smestiU!=null){
                    String putanja=smestiU.getAbsolutePath();
                    System.out.println(putanja);
                    //ime fajla
                    String izabran="greska:-1";
                    for(String f: finalFajlovi){
                        if(fajl.equals(f.split(":")[0])) izabran=f;
                    }
                    preuzmiFajl(izabran,new File(putanja+File.separator+fajl));
                }
                }else MessageBox.show("Izaberite fajl","Upozorenje");
        });


        //Vbox za prikaz drugog zadatka
        HBox fajloviPane=new HBox(lvprikaz,btnPreuzmi);



        //3. zadatak
        //matrica za prikaz
        String lavirint="@@@@@@@@@" +
                        "@     R @" +
                        "@ @@@@@ @" +
                        "@  @    E" +
                        "@@ @ @@@@" +
                        "@       @" +
                        "@ @@@@@ @" +
                        "@       @" +
                        "@@@@@@@@@";
        String[] niz=lavirint.split("");
        int c=0;
        VBox matrica=new VBox();
        //postavljanje izmenjive matrice
        for(int i=0;i<9;i++){
            HBox redovi=new HBox();
            for(int j=0;j<9;j++){
                TextField t=new TextField(niz[c++]);
                t.setPrefColumnCount(1);
                t.setOnMouseClicked(event -> t.clear());
                redovi.getChildren().add(t);

            }
            matrica.getChildren().add(redovi);
        }

        //Polje za unos poruke
        TextArea taResenje = new TextArea();
        taResenje.setWrapText(true);
        taResenje.setMaxWidth(400);
        taResenje.setMinWidth(400);


        //Dugme za slanje poruke
        Button btnSend = new Button("Pronadji put");
        btnSend.setOnAction(event -> {
            String postavka="";
            //citanje podataka iz matrice
            for(int i=0;i<9;i++){
                HBox redovi=(HBox) matrica.getChildren().get(i);
                for(int j=0;j<9;j++){
                    TextField t=(TextField) redovi.getChildren().get(j);
                    if(t.getText().isEmpty() || !t.getText().equals("R") && !t.getText().equals("E") && !t.getText().equals("@")) {
                            t.setText(" ");//postavljanje samo validnih karaktera
                    }
                    if(i==0 || i==8 || j==0 || j==8){
                        if(t.getText().equals(" ")){t.setText("@");}//ogranicavanje ako se obrisu zidovi
                    }
                    postavka+=t.getText();
                }

            }
            if(postavka.contains("R")){//validnost robota
                if(postavka.split("R").length>2){
                    MessageBox.show("U lavirintu su dva robota","Nevalidni parametri");
                    return;
                }
            }else{
                MessageBox.show("Postavite robota","Upozorenje");
                return;
            }
            if(postavka.contains("E")){//validnost izlaza
                if(postavka.split("E").length>2){
                    MessageBox.show("U lavirintu su dva izlaza","Nevalidni parametri");
                    return;
                }
            }else {
                MessageBox.show("Postavite izlaz","Upozorenje");
                return;
            }

            System.out.println(postavka);
            String reseno=komunikacija("lavirint###"+postavka);
            taResenje.setText(reseno);
        });

        //Hbox za unos poruke
        HBox lavirintPane = new HBox(10, matrica, taResenje,btnSend);

        //VBox za ceo raspored
        VBox pane = new VBox(20, panePalindrom,fajloviPane, lavirintPane);
        Scene scena = new Scene(pane);
        primaryStage.setScene(scena);
        primaryStage.show();

    }

    public String komunikacija(String request){
        String response="ako se ovo pokaze greska server nije pokrenut";
        try {
        // odredi adresu racunara sa kojim se povezujemo (ovde lokalno)
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            // otvori socket prema serveru
            Socket sock = new Socket(addr, TCP_PORT);
            // inicijalizuj ulazni stream
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            // inicijalizuj izlazni stream
            PrintWriter out =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            sock.getOutputStream())), true);
            // posalji zahtev
            out.println(request);
            // procitaj odgovor
            response = in.readLine();
            // zatvori konekciju
            in.close();
            out.close();
            sock.close();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return response;
    }

    public void preuzmiFajl(String request,File location) {

        Socket sock = null;
        int velicina=Integer.parseInt(request.split(":")[1]);
        String imeFajla=request.split(":")[0];

        try {
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            sock = new Socket(addr, TCP_PORT);
            PrintWriter out =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            sock.getOutputStream())), true);
            //slanje zahteva za trazeni fajl
            out.println("files###file###"+imeFajla);

            // receive file
            InputStream in = sock.getInputStream();
            DataInputStream clientData = new DataInputStream(in);
            OutputStream output = new FileOutputStream(location);
            int bytesRead;
            long size = velicina;
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            in.close();

            System.out.println("File "+imeFajla+" received from Server.");
            MessageBox.show("Fajl "+imeFajla+" uspešno preuzet","Preuzimanje");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

                try {
                    if (sock != null) sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    }

}


