package br.com.loja.florescer.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.loja.florescer.indicador.StatusPagamentoIndicador;
import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;

public record PedidoView(long codigoPedido, String cpfCliente, LocalDateTime dataPedido, List<ItemPedidoView> itensPedido, TipoFormaPagamentoIndicador tipoPagamento, StatusPagamentoIndicador statusPagamento, EnderecoView enderecoEntrega, BigDecimal valorFrete, BigDecimal valorTotalItens, BigDecimal valorTotalPagamento) { }
