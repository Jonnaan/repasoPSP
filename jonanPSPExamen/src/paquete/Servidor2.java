package paquete;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Servidor2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Peticion> peticiones = new ArrayList<Peticion>();
		Scanner sc = new Scanner(System.in);
		String res = "";
		
		do {
			Peticion peticion = new Peticion();
			System.out.println("�Desea dar de alta una petici�n? S/N");
			res = sc.nextLine();
			if(res.equalsIgnoreCase("S"))
			{
				
				System.out.println("ID: ");
				String idString;
				do {
					idString = sc.nextLine();
					
				}while(!isNumeric(idString));
				
				int iD= Integer.parseInt(idString);
				peticion.setId(iD);
				
			
				System.out.println("Clave: ");
				String clave = sc.nextLine();
				peticion.setClave(clave);
				
				System.out.println("URL: ");
				String url = sc.nextLine();
				peticion.setImagen(url);
				
				peticiones.add(peticion);
			}
	
		} while (!res.equalsIgnoreCase("N"));
		
		   try {
	            ServerSocket serv = new ServerSocket(5000);
	            System.out.println("Servidor iniciado...");
	            
	            while (!serv.isClosed()) {
	            	
	                Socket cli = serv.accept();
	               
	                new HiloServidor(cli, peticiones).start();
	            	
	            }
	            serv.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
			sc.close();
			
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

}