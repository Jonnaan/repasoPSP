package paquete;
import java.io.Serializable;

public class Peticion implements Serializable{
	private static final long serialVersionUID = 1L;
	int id;
	String clave;
	String imagen;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public Peticion(int id, String clave, String imagen) {
		this.id = id;
		this.clave = clave;
		this.imagen = imagen;
	}
	public Peticion() {
	}
	
}
