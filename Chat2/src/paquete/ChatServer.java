package paquete;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    // Usamos un conjunto sincronizado para almacenar los clientes conectados.
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        int port = 5000;
        System.out.println("Servidor de chat iniciado en el puerto " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // El servidor estará siempre a la espera de nuevos clientes.
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + socket);
                
                // Se crea y lanza un nuevo hilo para cada cliente.
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Envía el mensaje a todos los clientes conectados, excepto (opcionalmente) al remitente.
    public static void broadcast(String message, ClientHandler excludeClient) {
        synchronized(clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (client != excludeClient) {
                    client.sendMessage(message);
                }
            }
        }
    }
    
    // Elimina un cliente de la lista de conectados.
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Cliente desconectado: " + clientHandler.getClientName());
    }
}
