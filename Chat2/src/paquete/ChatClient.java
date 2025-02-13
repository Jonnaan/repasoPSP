package paquete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        String hostname = "localhost";  // Cambia esto si el servidor está en otra máquina.
        int port = 5000;
        
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Conectado al servidor de chat.");
            
            // Se lanzan dos hilos: uno para leer mensajes del servidor y otro para enviar mensajes.
            new ReadThread(socket).start();
            new WriteThread(socket).start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Hilo encargado de leer mensajes del servidor.
class ReadThread extends Thread {
    private BufferedReader in;
    private Socket socket;
    
    public ReadThread(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                String response = in.readLine();
                if (response == null) break;
                System.out.println(response);
            } catch (IOException e) {
                System.out.println("Error leyendo del servidor.");
                break;
            }
        }
    }
}

// Hilo encargado de enviar mensajes al servidor.
class WriteThread extends Thread {
    private PrintWriter out;
    private Socket socket;
    private Scanner scanner;
    
    public WriteThread(Socket socket) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        // Este ciclo permite enviar mensajes continuamente.
        while (true) {
            String message = scanner.nextLine();
            out.println(message);
        }
    }
}
