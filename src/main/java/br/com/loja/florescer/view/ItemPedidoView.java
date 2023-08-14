package br.com.loja.florescer.view;

import java.math.BigDecimal;

public record ItemPedidoView(String nomeProduto, int quantidade, BigDecimal valor) { }
