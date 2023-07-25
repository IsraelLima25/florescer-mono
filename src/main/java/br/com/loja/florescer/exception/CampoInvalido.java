package br.com.loja.florescer.exception;

public class CampoInvalido {
	
	private String campo;
    private String mensagem;

    public CampoInvalido(String campo, String mensagem) {
        this.campo = campo;
        this.mensagem = mensagem;
    }

    public String getCampo() {
        return campo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
