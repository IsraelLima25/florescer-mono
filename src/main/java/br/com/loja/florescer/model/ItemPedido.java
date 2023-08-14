package br.com.loja.florescer.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_pedido_produto")
public class ItemPedido implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@EmbeddedId
	private ItemPedidoPK id = new ItemPedidoPK();
	
	@Column(name = "quantidade", nullable = false)
    private int quantidade;
	
	@Deprecated
	public ItemPedido() {
	}

	public ItemPedido(Produto produto, Pedido pedido, int quantidade) {
		id.setPedido(pedido);
		id.setProduto(produto);
		this.quantidade = quantidade;
	}
	
	public ItemPedidoPK getId() {
		return id;
	}
	
	public int getQuantidade() {
		return quantidade;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemPedido other = (ItemPedido) obj;
		return Objects.equals(id, other.id);
	}

}
