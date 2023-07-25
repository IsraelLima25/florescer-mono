package br.com.loja.florescer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.loja.florescer.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> { }
