package br.com.loja.florescer.form;

import java.util.List;

import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PedidoForm(@NotBlank String cpfCliente, @Valid @NotNull EnderecoForm enderecoForm, @NotNull TipoFormaPagamentoIndicador tipoPagamento, @NotNull List<PedidoProdutoForm> produtos, String Observacao) { }
