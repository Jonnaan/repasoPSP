package paquete;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class HiloServidor extends Thread {
    private Socket cli;
    private ArrayList<Peticion> peticiones;
    private String clave;
    private Peticion peticionCli;

    public HiloServidor(Socket cliente, ArrayList<Peticion> peticiones) {
        this.cli = cliente;
        this.peticiones = peticiones;
    }

    @Override
    public void run() {
        try {
            // 1) Primero creamos los ObjectOutput/ObjectInput
            ObjectOutputStream salida = new ObjectOutputStream(cli.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(cli.getInputStream());

            // 2) Luego DataOutput/DataInput para intercambiar cadenas
            DataOutputStream dos = new DataOutputStream(cli.getOutputStream());
            DataInputStream dis = new DataInputStream(cli.getInputStream());

            // Preguntamos al cliente por el ID
            dos.writeUTF("¿Qué imagen quiere obtener? 0 para salir");

            // Leemos la respuesta (ID en String)
            String primerRecibido = dis.readUTF();
            int recivo = Integer.valueOf(primerRecibido);

            // Si es 0, salimos
            if (recivo == 0) {
                dos.writeUTF("Saliendo del programa");
                cli.close();
                return; 
            }

            // Buscamos el ID en la lista
            Peticion peticionEncontrada = null;
            for (Peticion p : peticiones) {
                if (p.getId() == recivo) {
                    peticionEncontrada = p;
                    break; // dejamos de buscar
                }
            }

            // Si no se ha encontrado el ID, avisamos y cerramos
            if (peticionEncontrada == null) {
                dos.writeUTF("No se ha encontrado ninguna petición con ese ID. Salgo del programa");
                cli.close();
                return; 
            }

            // Si encontramos el ID, pedimos la contraseña
            dos.writeUTF("¿Cuál es la contraseña?");
            clave = peticionEncontrada.getClave();  // la clave guardada (texto plano o ya hasheada)

            // Leemos la contraseña que envía el cliente (ya hasheada por el propio cliente)
            String contrasenaRecibida = dis.readUTF();

            // Hasheamos la clave almacenada en servidor (por si la guardaste en texto plano)
            // Si en tu caso ya la guardas hasheada, podrías comparar directo.
            String claveSHA = hashPassword(clave);

            if (contrasenaRecibida.equalsIgnoreCase(claveSHA)) {
                // Contraseña correcta: enviamos la Peticion al cliente
                salida.writeObject(peticionEncontrada);

                // Preguntamos si quiere modificar la URL
                dos.writeUTF("Puedes modificar la URL. Escribe la nueva URL o 0 para no modificar");

                peticionCli = (Peticion) entrada.readObject();
                peticionEncontrada.setImagen(peticionCli.getImagen());

                dos.writeUTF("Imagen recibida. Ahora la URL en el servidor es: " + peticionEncontrada.getImagen());
            } else {
                dos.writeUTF("La contraseña no es correcta");
                cli.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Hash SHA-256 para contraseñas
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
