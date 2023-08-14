package br.com.loja.florescer.view;

import java.math.BigDecimal;

public record ProdutoView(String descricao, BigDecimal preco, int quantidadeEstoque, String filial) { }
