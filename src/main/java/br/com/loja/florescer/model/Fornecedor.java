package br.com.loja.florescer.model;

import java.util.Objects;

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
@Table(name = "tbl_fornecedor")
public class Fornecedor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="nome", nullable = false)
	private String nome;
	
	@Column(name="cnpj", nullable = false)
	private String cnpj;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_endereco", referencedColumnName = "id", nullable = false)
	private Endereco endereco;
	
	@Deprecated
	public Fornecedor() {
	}
	
	public Fornecedor(Long id, String nome, String cnpj, Endereco endereco) {
		this.nome = nome;
		this.cnpj = cnpj;
		this.endereco = endereco;
		this.id = id;
	}

	public Fornecedor(String nome, String cnpj, Endereco endereco) {
		this.nome = nome;
		this.cnpj = cnpj;
		this.endereco = endereco;
	}
	
	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}
	
	public String getCnpj() {
		return cnpj;
	}

	@Override
	public String toString() {
		return "Fornecedor [nome=" + nome + ", cnpj=" + cnpj + ", endereco=" + endereco + "]";
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
		Fornecedor other = (Fornecedor) obj;
		return Objects.equals(id, other.id);
	}
	
}
