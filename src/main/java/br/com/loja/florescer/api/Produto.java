package br.com.loja.florescer.api;

import java.math.BigDecimal;

public class Produto {
	

	private String descricao;
	private BigDecimal preco;
	
	public Produto(String descricao, BigDecimal preco) {
		this.descricao=descricao;
		this.preco=preco;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	@Override
	public String toString() {
		return "Produto [descricao=" + descricao + ", preco=" + preco + "]";
	}
	
}
