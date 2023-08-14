package br.com.loja.florescer.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_reserva_estoque")
public class ReservaEstoque {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", nullable = false)
	private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name = "id_produto", referencedColumnName = "id", nullable = false)
	private Produto produto;
	
	@Column(name = "quantidade", nullable = false)
	private int quantidade;
	
	@Deprecated
	public ReservaEstoque() {
		
	}

	public ReservaEstoque(Pedido pedido, Produto produto, int quantidade) {
		this.pedido = pedido;
		this.produto = produto;
		this.quantidade = quantidade;
	}
	
	public Pedido getPedido() {
		return pedido;
	}
	
	public Produto getProduto() {
		return produto;
	}
	
	public int getQuantidade() {
		return quantidade;
	}
	
	public long getId() {
		return id;
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
		ReservaEstoque other = (ReservaEstoque) obj;
		return Objects.equals(id, other.id);
	}
	
}
