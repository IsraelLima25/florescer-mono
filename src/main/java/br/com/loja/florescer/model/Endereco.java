package br.com.loja.florescer.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_endereco")
public class Endereco {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="cep", nullable = false)
	private String cep;
	
	@Column(name="logradouro", nullable = false)
	private String logradouro;
	
	@Column(name="complemento")
	private String complemento;
	
	@Column(name="bairro", nullable = false)
	private String bairro;
	
	@Column(name="localidade", nullable = false)
	private String localidade;
	
	@Column(name="uf", nullable = false)
	private String uf;
	
	@Deprecated
	public Endereco() {
		super();
	}

	public Endereco(String cep, String logradouro, String complemento, String bairro, String localidade, String uf) {
		this.cep = cep;
		this.logradouro = logradouro;
		this.complemento = complemento;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
	}

	public Endereco(Long id, String cep, String logradouro, String complemento, String bairro, String localidade, String uf) {
		this.id = id;
		this.cep = cep;
		this.logradouro = logradouro;
		this.complemento = complemento;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
	}

	@Override
	public String toString() {
		return "Endereco [cep=" + cep + ", logradouro=" + logradouro + ", complemento=" + complemento + ", bairro="
				+ bairro + ", localidade=" + localidade + ", uf=" + uf + "]";
	}
	
	public Long getId() {
		return id;
	}
	
	public String getCep() {
		return cep;
	}
	
	public String getLogradouro() {
		return logradouro;
	}
	
	public String getComplemento() {
		return complemento;
	}
	
	public String getBairro() {
		return bairro;
	}
	
	public String getLocalidade() {
		return localidade;
	}
	
	public String getUf() {
		return uf;
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
		Endereco other = (Endereco) obj;
		return Objects.equals(id, other.id);
	}
	
}
