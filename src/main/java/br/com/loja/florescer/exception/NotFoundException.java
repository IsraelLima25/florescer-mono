package br.com.loja.florescer.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String campo;
	private String mensagem;

	public NotFoundException(String campo, String mensagem) {
		super(mensagem);
		this.campo = campo;
	}

	public String getMensagem() {
		return mensagem;
	}
	
	public String getCampo() {
		return campo;
	}

}
