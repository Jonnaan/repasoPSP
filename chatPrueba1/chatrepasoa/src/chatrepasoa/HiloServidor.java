package chatrepasoa;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class HiloServidor extends Thread {
    private Socket cli;
    private ArrayList<Usuario> usuarios;
    private DataInputStream dis;
    private DataOutputStream dos;

    public HiloServidor(Socket cliente, ArrayList<Usuario> usuarios) {
        this.cli = cliente;
        this.usuarios = usuarios;
    }

    public void run() {
        try {
            dis = new DataInputStream(cli.getInputStream());
            dos = new DataOutputStream(cli.getOutputStream());

            dos.writeUTF("Introduce tu nombre de usuario:");
            String nombre = dis.readUTF();

            dos.writeUTF("Introduce tu contraseña:");
            String contraSinHash = dis.readUTF();
            String contraHashC = dis.readUTF();
            String contraHashS = hashPassword(contraSinHash);

            if (!contraHashC.equals(contraHashS)) {
                dos.writeUTF("Contraseña incorrecta. Desconectando...");
                cli.close();
                return;
            }

            Usuario user = new Usuario(nombre, contraHashC, cli);
            usuarios.add(user);
            dos.writeUTF("Has iniciado sesión correctamente. Bienvenido " + nombre);

            while (true) {
                dos.writeUTF("Escribe un mensaje:");
                String mensaje = dis.readUTF();
                if (mensaje.equals("!logout")) {
                    dos.writeUTF("Desconectando " + user.getNombre());
                    usuarios.remove(user);
                    cli.close();
                    break;
                }
                enviarMensajeATodos(nombre + ": " + mensaje);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        }
    }

    private void enviarMensajeATodos(String mensaje) {
        for (Usuario u : usuarios) {
            try {
                DataOutputStream dosCliente = new DataOutputStream(u.getSocket().getOutputStream());
                dosCliente.writeUTF(mensaje);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
