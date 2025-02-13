package chatrepasoa;

import java.io.Serializable;
import java.net.Socket;


public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nombre;
    private String contraseñaHash;
    private Socket socket;

    public Usuario(String nombre, String contraseñaHash, Socket socket) {
        this.nombre = nombre;
        this.contraseñaHash = contraseñaHash;
        this.socket = socket;  
    }
    public Usuario() {

    }

    public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getContraseñaHash() {
		return contraseñaHash;
	}
	public void setContraseñaHash(String contraseñaHash) {
		this.contraseñaHash = contraseñaHash;
	}
	
	
	 public Socket getSocket() {
	        return socket; 
	    }

	    public void setSocket(Socket socket) {
	        this.socket = socket;  
	    }
	
	

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", contraseñaHash='" + contraseñaHash + '\'' +
                '}';
    }
}
