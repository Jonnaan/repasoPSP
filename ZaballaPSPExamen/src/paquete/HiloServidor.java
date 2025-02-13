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
    String clave ;
    ArrayList<Peticion> peticiones = new ArrayList<Peticion>();
    Peticion peticionCli;
    public HiloServidor(Socket cliente, ArrayList<Peticion> peticiones) {
        this.cli = cliente;
        this.peticiones = peticiones;
       
    }

    public void run() {
    	
    	ObjectOutputStream salida = null;
		try {
			salida = new ObjectOutputStream(cli.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
        
        	  ObjectInputStream entrada = new ObjectInputStream(cli.getInputStream());
            DataInputStream dis = new DataInputStream(cli.getInputStream());
            DataOutputStream dos = new DataOutputStream(cli.getOutputStream());
            dos.writeUTF("¿Que imagen quiere obtener? 0 para salir");
          
                String primerRecibido = dis.readUTF(); 
                int recivo=  Integer.valueOf(primerRecibido);
                if (recivo==0) {
                	
                    cli.close();
                 
                }else {
                	
                
               for(Peticion p: peticiones){
                		int id = p.getId();
                
               		 if(id==recivo) {
               			dos.writeUTF("¿Cual es la contraseña?");
               			 clave = p.getClave();
               		
               		 }else {
               			dos.writeUTF("No se ha encontrado ningun peticion con ese ID. Salgo del programa");
               			 cli.close();
               		 }
               		
               	  	
                String contrasenaRecivida=dis.readUTF();
     			String claveSHA = hashPassword(clave);
     			
     			if(contrasenaRecivida.equalsIgnoreCase(claveSHA)) {
     				
     				salida.writeObject(p);
     				dos.writeUTF("Puedes modificar la URL. 0 para no modificar");
     			}
     			
     			
     			else {
           			dos.writeUTF("La contraseña no es correcta");
     				cli.close();
     				
     			}

                 peticionCli = (Peticion) entrada.readObject();
     			dos.writeUTF("Imagen recibida");  			
               }  
                }
        
                }
         catch (IOException e) {
         
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
