import java.io.*; import java.net.*;
import java.util.ArrayList;

public class Server {
    public static final int TCP_PORT = 9000;
    public static void main(String[] args) {
        try {
            int clientCounter = 0;
            ServerSocket ss = new ServerSocket(TCP_PORT);
            System.out.println("Server running...");
            while (true) {
                Socket sock = ss.accept();
                System.out.println("Request accepted:"+ (++clientCounter));
                ServerThread st = new ServerThread(sock);
            }
        } catch (Exception ex) { ex.printStackTrace(); } } }