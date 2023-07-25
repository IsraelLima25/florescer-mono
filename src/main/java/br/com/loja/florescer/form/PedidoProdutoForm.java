package br.com.loja.florescer.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoProdutoForm(@Positive @NotNull Long id, @Positive @NotNull @Positive Integer quantidade) { }
