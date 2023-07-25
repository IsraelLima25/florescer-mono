package br.com.loja.florescer.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String mensagem;

	public BusinessException(String mensagem) {
		super(mensagem);
	}
	
	public String getMensagem() {
		return mensagem;
	}
}
