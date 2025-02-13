package paquete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    // Método para enviar mensajes al cliente.
    public void sendMessage(String message) {
        out.println(message);
    }
    
    @Override
    public void run() {
        try {
            // Inicializamos los flujos para comunicación de texto.
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // Se pide al cliente que ingrese su nombre.
            out.println("Bienvenido al chat. Ingrese su nombre:");
            clientName = in.readLine();
            System.out.println("El cliente " + clientName + " se ha unido al chat.");
            ChatServer.broadcast(clientName + " se ha unido al chat.", this);
            
            // Ciclo para recibir mensajes enviados por el cliente.
            String message;
            while ((message = in.readLine()) != null) {
                String formattedMessage = clientName + ": " + message;
                System.out.println(formattedMessage);
                ChatServer.broadcast(formattedMessage, this);
            }
        } catch (IOException e) {
            System.out.println("Error en la conexión con " + clientName);
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
            ChatServer.broadcast(clientName + " ha salido del chat.", this);
        }
    }
}
