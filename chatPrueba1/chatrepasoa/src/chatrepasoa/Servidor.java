package chatrepasoa;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Servidor {
    public static void main(String[] args) {
        ArrayList<Usuario> usuarios = new ArrayList<>(); 
        Scanner sc = new Scanner(System.in);
        String res;

        System.out.println("¿Iniciar el servidor? S/N");
        res = sc.nextLine();
        
        if (res.equalsIgnoreCase("S")) {
            try (ServerSocket serv = new ServerSocket(5000)) {
                System.out.println("Servidor iniciado en el puerto 5000...");

                while (true) {
                    Socket cli = serv.accept();
                    System.out.println("Nuevo cliente conectado...");
                    new HiloServidor(cli, usuarios).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Servidor apagado.");
        }

        sc.close();
    }
}
