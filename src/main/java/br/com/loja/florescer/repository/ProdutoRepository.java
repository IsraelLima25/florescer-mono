package br.com.loja.florescer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.loja.florescer.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{ }
