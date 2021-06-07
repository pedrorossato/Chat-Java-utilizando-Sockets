package comunication;
import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public int tipo;
	public String remetente;
	public String mensagem;
	public Message(int tipo, String remetente, String mensagem) {
		this.tipo = tipo;
		this.remetente = remetente;
		this.mensagem = mensagem;
	}
	public Message(int tipo, String mensagem) {
		this.tipo = tipo;
		this.mensagem = mensagem;
	}
	public String getRemetente() {
		return remetente;
	}
	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
}
