package comunication;
import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public TypeOfMessage tipo;
	public char[] remetente;
	public String mensagem;
	
	public Message(TypeOfMessage tipo, char[] remetente, String mensagem) {
		this.tipo = tipo;
		this.remetente = remetente;
		this.mensagem = mensagem;
	}
	public Message(TypeOfMessage tipo) {
		this.tipo = tipo;
	}
	public Message(char[] remetente) {
		this.remetente = remetente;
	}
	public Message(String mensagem) {
		this.mensagem = mensagem;
	}
	public Message(TypeOfMessage tipo, char[] remetente) {
		this.tipo = tipo;
		this.remetente = remetente;
	}
	public Message(TypeOfMessage tipo, String mensagem) {
		this.tipo = tipo;
		this.mensagem = mensagem;
	}
	public Message(char[] remetente,String mensagem) {
		this.remetente = remetente;
		this.mensagem = mensagem;
	}
	public char[] getRemetente() {
		return remetente;
	}
	public void setRemetente(char[] remetente) {
		this.remetente = remetente;
	}
	public TypeOfMessage getTipo() {
		return tipo;
	}
	public void setTipo(TypeOfMessage tipo) {
		this.tipo = tipo;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
}
