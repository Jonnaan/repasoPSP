package paquete;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Cliente2 {

    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String Host = "localhost";
        int Puerto = 5000; // puerto remoto
        Socket cli = null;

        try {
            cli = new Socket(Host, Puerto);
            System.out.println("--Cliente--");
            System.out.println("Conectado a: " + cli.getRemoteSocketAddress());

            // 1) Primero creamos ObjectOutputStream / ObjectInputStream (mismo orden que el server)
            ObjectOutputStream salida = new ObjectOutputStream(cli.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(cli.getInputStream());

            // 2) Luego DataOutputStream / DataInputStream para mensajes de texto
            DataOutputStream dos = new DataOutputStream(cli.getOutputStream());
            DataInputStream dis = new DataInputStream(cli.getInputStream());

            // El servidor nos pregunta por el ID
            System.out.println(dis.readUTF()); // "¿Qué imagen quiere obtener? 0 para salir"
            String idPeti;
            do {
                idPeti = scanner.nextLine();
                // Si el usuario pone 0, se envía 0 y salimos
                if (idPeti.equals("0")) {
                    dos.writeUTF(idPeti); // enviamos "0"
                    System.out.println(dis.readUTF()); // "Saliendo del programa"
                    cli.close();
                    System.exit(0);
                }
            } while (!isNumeric(idPeti));

            // Enviamos el ID al servidor
            dos.writeUTF(idPeti);

            // Leemos la pregunta de contraseña
            System.out.println(dis.readUTF()); // "¿Cuál es la contraseña?"
            String contra2SinSHA = scanner.nextLine();

            // Hasheamos la contraseña para enviarla
            String contraHASH = hashPassword(contra2SinSHA);
            dos.writeUTF(contraHASH);

            // Ahora recibimos la Peticion si la contraseña es correcta
            // (Si no es correcta, el server nos manda un mensaje y cierra)
            Peticion peticion = null;
            try {
                peticion = (Peticion) entrada.readObject();
            } catch (IOException ex) {
                // Si falla aquí, probablemente el server cerró porque la contraseña estaba mal
                System.out.println("Error recibiendo Peticion (¿Contraseña incorrecta?). Se cierra.");
                cli.close();
                System.exit(0);
            }

            // Leemos el mensaje: "Puedes modificar la URL..."
            System.out.println(dis.readUTF());
            String urlPeti = scanner.nextLine();

            // Si el usuario teclea "0", no se modifica la URL
            if (!urlPeti.equalsIgnoreCase("0")) {
                peticion.setImagen(urlPeti);
            }

            // Enviamos de nuevo el objeto Peticion (ya sea con la misma URL o la nueva)
            salida.writeObject(peticion);

            // Leemos la confirmación final del servidor
            System.out.println(dis.readUTF());

            // Cerramos
            cli.close();
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumeric(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
            System.out.println("Introduce un número");
        }
        return resultado;
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
            e.printStackTrace();
            return null;
        }
    }
}
