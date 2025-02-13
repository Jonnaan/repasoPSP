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
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		String Host = "localhost";
		int Puerto = 5000;// puerto remoto
		Socket cli;
        try {
        	
             cli = new Socket(Host, Puerto);
            System.out.println("Conexión realizada... 'Cliente'");
            ObjectInputStream entrada = new ObjectInputStream(cli.getInputStream());
            ObjectOutputStream salida = null;
    		try {
    			salida = new ObjectOutputStream(cli.getOutputStream());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

            DataOutputStream dos = new DataOutputStream(cli.getOutputStream());
            DataInputStream dis = new DataInputStream(cli.getInputStream());
         

			System.out.println(dis.readUTF());
			String envio1;
							do {
								envio1 = scanner.nextLine();
							}while(isNumeric(envio1)==false);
						 
	                	 dos.writeUTF(envio1);
	                	 System.out.println(dis.readUTF());
	                	 
	                	 String envio2SinSHA = scanner.nextLine();    
	                	 
	                	 String envio2 = hashPassword(envio2SinSHA);
	                	 dos.writeUTF(envio2);

        
	                    Peticion peticion = (Peticion) entrada.readObject();
	                    
	                    
	                    System.out.println(dis.readUTF());
	                    String envio3 =scanner.nextLine();
	                    String cero = "0";
	                	 if(envio3.equalsIgnoreCase(cero)) {
	                	 salida.writeObject(peticion);
	                	 }else {
	                	 peticion.setImagen(envio3);
	                	 salida.writeObject(peticion);
	                	 }
	                	 
	                	 System.out.println(dis.readUTF());
						 }
        
	                	
	                
	                catch (IOException e) {
	                    e.printStackTrace();
	                }
        scanner.close();
	}
	

	 
	 public static boolean isNumeric(String cadena) {

	        boolean resultado;

	        try {
	            Integer.parseInt(cadena);
	            resultado = true;
	        } catch (NumberFormatException excepcion) {
	            resultado = false;
	            System.out.println("Introduce un numero");
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
	         
	                
	                
	                
	             
	     
	        
	    
	