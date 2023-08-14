package br.com.loja.florescer.model;

import java.time.LocalDate;
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
@Table(name = "tbl_cliente")
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="nome", nullable = false)
	private String nome;
	
	@Column(name="cpf", nullable = false)
	private String cpf;
	
	@Column(name="rg", nullable = false)
	private String rg;
	
	@Column(name="data_nascimento", nullable = false)
	private LocalDate dataNascimento;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_endereco", referencedColumnName = "id", nullable = false)
	private Endereco endereco;
	
	@Deprecated
	public Cliente() {
	}

	public Cliente(String nome, String cpf, String rg, LocalDate dataNascimento, Endereco endereco) {
		this.nome = nome;
		this.cpf = cpf;
		this.rg = rg;
		this.dataNascimento = dataNascimento;
		this.endereco = endereco;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getCpf() {
		return cpf;
	}
	
	public String getRg() {
		return rg;
	}
	
	public LocalDate getDataNascimento() {
		return dataNascimento;
	}
	
	public Endereco getEndereco() {
		return endereco;
	}
	
	public void atualizarCliente(String nome, String cpf, String rg, LocalDate dataNascimento, Endereco endereco) {
		this.nome = nome;
		this.cpf=cpf;
		this.rg=rg;
		this.dataNascimento=dataNascimento;
		this.endereco=endereco;
	}

	@Override
	public String toString() {
		return "Cliente [nome=" + nome + ", cpf=" + cpf + ", rg=" + rg + ", dataNascimento=" + dataNascimento
				+ ", endereco=" + endereco + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(cpf, id, rg);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(cpf, other.cpf) && Objects.equals(id, other.id) && Objects.equals(rg, other.rg);
	}
	
	
	
}
