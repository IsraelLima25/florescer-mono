package br.com.loja.florescer.api;


public class Item {

	private Produto produto;
	private Integer quantidade;
	
	public Item(Produto produto, Integer quantidade) {
		this.produto = produto;
		this.quantidade = quantidade;
	}
	
	public Produto getProduto() {
		return produto;
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}

	@Override
	public String toString() {
		return "Item [produto=" + produto + ", quantidade=" + quantidade + "]";
	}
	
	
	
}
