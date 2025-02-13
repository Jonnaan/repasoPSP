package chatrepasoa;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String nombre, contrasena;
        String serverAddress = "localhost"; 
        int port = 5000; 

        try (Socket socket = new Socket(serverAddress, port)) {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            System.out.println(dis.readUTF());
            nombre = sc.nextLine();
            dos.writeUTF(nombre); 

            System.out.println(dis.readUTF());
            contrasena = sc.nextLine();
            dos.writeUTF(contrasena);
            dos.writeUTF(hashPassword(contrasena));

            String respuesta = dis.readUTF();
            System.out.println(respuesta);

            if (respuesta.contains("incorrecta") || respuesta.contains("Desconectando")) {
                socket.close();
                return;
            }

            while (true) {
                System.out.println(dis.readUTF());
                String mensaje = sc.nextLine();
                if (mensaje.equals("!logout")) {
                    dos.writeUTF(mensaje);
                    System.out.println("Has cerrado sesión.");
                    break; 
                }
                dos.writeUTF(mensaje);
                System.out.println("Esperando respuesta...");
                System.out.println(dis.readUTF());
            }
        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }

        sc.close();
    }
    
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash de la contraseña", e);
        }
    }
}

