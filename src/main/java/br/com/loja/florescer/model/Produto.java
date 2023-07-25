package br.com.loja.florescer.model;

import java.math.BigDecimal;
import java.util.Objects;

import br.com.loja.florescer.exception.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_produto")
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "preco", nullable = false)
	private BigDecimal preco;

	@Column(name = "quantidade_estoque", nullable = false)
	private Integer quantidadeEstoque;

	@Column(name = "sigla_filial", nullable = false)
	private String filial;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_fornecedor", referencedColumnName = "id", nullable = false)
	private Fornecedor fornecedor;

	@Deprecated
	public Produto() {
	}

	public Produto(String descricao, BigDecimal preco, Integer quantidadeEstoque, String filial,
			Fornecedor fornecedor) {
		this.descricao = descricao;
		this.preco = preco;
		this.quantidadeEstoque = quantidadeEstoque;
		this.filial = filial;
		this.fornecedor = fornecedor;
	}
	
	public Long getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public Integer getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public String getFilial() {
		return filial;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}
	
	public void abater(Integer quantidade) {
		if(quantidade <= 0) {
			throw new BusinessException("Quantidade de abatimento inválida");
		}
		this.quantidadeEstoque-=quantidade;
	}
	
	public void devolver(Integer quantidade) {
		if(quantidade <= 0) {
			throw new BusinessException("Quantidade de devolução inválida");
		}
		this.quantidadeEstoque+=quantidade;
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
		Produto other = (Produto) obj;
		return Objects.equals(id, other.id);
	}

}
