import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerThread extends Thread {

    public ServerThread(Socket sock) {
        this.sock = sock;
        try {
            in = new BufferedReader( // inicijalizuj ulazni stream
                    new InputStreamReader(
                            sock.getInputStream()));
            out = new PrintWriter( // inicijalizuj izlazni stream
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    sock.getOutputStream())), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        start();
    }

    public void run() {
        try {
            // procitaj zahtev
            String request = in.readLine();
            //podaci
            String[] data = request.split("###");

            //prvi zadatak
            String tmp = "";
            if (data[0].equals("palindrom")) {
                String str1 = data[1].replaceAll(" ", "").toLowerCase();
                String[] pom = str1.split("");
                String str2 = "";
                for (int i = pom.length - 1; i >= 0; i--) {
                    str2 += pom[i];
                }
                if (str1.equals(str2)) out.println("Recenica jeste palindrom!");
                else out.println("Recenica nije palindrom!");
                System.out.println(str2);
            }


            //drugi zadatak
            if (data[0].equals("files")) {
                String podaci = "Nema fajlova za prikaz";

                if (data[1].equals("check")) {
                    podaci="";
                    try {
                        File file = new File(folderPath); // formiraj odgovor
                        if (file.exists()) {
                            if (file.isDirectory()) {
                                File[] files = file.listFiles();
                                for (int i = 0; i < files.length; i++)
                                    podaci += files[i].getName()+":"+files[i].length() + "###";
                            } else {
                                podaci = "Error: " + folderPath + " is a file\n";
                            }
                        } else {
                            podaci = "Error: path does not exist\n";
                        }
                        out.println(podaci); // zatvori konekciju
                        System.out.println(podaci);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if(data[1].equals("file")){
                    String download=data[2];
                    System.out.println(download);
                    if(download==null) {
                        System.out.println("greska");
                        return;
                    }

                    //slanje fajla
                    try {
                        File myFile = new File(folderPath+File.separator+download);
                        byte[] mybytearray = new byte[(int) myFile.length()];

                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);

                        DataInputStream dis = new DataInputStream(bis);
                        dis.readFully(mybytearray, 0, mybytearray.length);


                        OutputStream os = sock.getOutputStream();

                        DataOutputStream dos = new DataOutputStream(os);
                        dos.write(mybytearray, 0, mybytearray.length);
                        dos.flush();
                        System.out.println("File "+download+" sent to client.");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }


        //treci zadatak
        if (data[0].equals("lavirint")) {
            String[] niz = data[1].split("");
            String[][] matrica = new String[9][9];
            int x = -1;
            int y = -1;
            int c = 0;
            //sta je stiglo
            for (String k : niz) {
                System.out.printf(k);
            }
            System.out.println("");

            //prebaceno u matricu i prikazano iz nje
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {

                    matrica[i][j] = niz[c++];
                    System.out.printf(matrica[i][j]);
                }
            }
            System.out.println("\n");
            //postavka matrice za pretragu
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {

                    if (matrica[i][j].equals("R")) {
                        pocetak = new Coord(i, j);
                        matrica[i][j] = Free;
                    }
                    if (matrica[i][j].equals("E")) {
                        izlaz = new Coord(i, j);
                        matrica[i][j] = Free;
                    }

                }
            }
            String resenje = "";

            //trazenje izlaza
            if (Solve(matrica, pocetak.x, pocetak.y)) {
                resenje = "";
                for (int i = putanja.size() - 1; i >= 0; i--) {
                    resenje += putanja.get(i);
                }
            } else {
                resenje = "Nema puta do izlaza";
            }
            System.out.println(resenje);
            out.println(resenje);
        }

        // zatvori konekciju
        in.close();
        out.close();
        sock.close();
    } catch(
    Exception ex)

    {
        ex.printStackTrace();
    }

}

    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;
    private final String folderPath="DATA_SERVER";//lokacija foldera sa fajlovima za slanje

    //za slanje fajlova
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;

    //postavka paramerata za lavirint
    private final String Robot = "R";
    private final String Free = " ";
    private Coord izlaz;//izlaz iz lavirinta
    private Coord pocetak;//pocetka pozicija u lavirintu
    private ArrayList<String> putanja = new ArrayList<>();//lista skretanja za lavirint



    boolean Solve(String[][] lavirint, int x, int y) {

        lavirint[x][y] = Robot;
        prikaz(lavirint);

        // Provera da li je izlaz
        if (x == izlaz.x && y == izlaz.y) {
            return true;
        }

        // Rekurzivno trazenje izlaza
        if (y < 9 && lavirint[x][y + 1].equals(Free) && Solve(lavirint, x, y + 1)) {
            putanja.add("desno ");
            return true;
        }
        if (x < 9 && lavirint[x + 1][y].equals(Free) && Solve(lavirint, x + 1, y)) {
            putanja.add("dole ");
            return true;
        }
        if (x > 0 && lavirint[x - 1][y].equals(Free) && Solve(lavirint, x - 1, y)) {
            putanja.add("gore ");
            return true;
        }

        if (y > 0 && lavirint[x][y - 1].equals(Free) && Solve(lavirint, x, y - 1)) {
            putanja.add("levo ");
            return true;
        }

        // Vracanje unazad
        lavirint[x][y] = Free;

        if (!putanja.isEmpty()) {
            putanja.remove(0);
        }

        prikaz(lavirint);

        return false;
    }

    void prikaz(String[][] matrix) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.printf(matrix[i][j]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");

    }
}

