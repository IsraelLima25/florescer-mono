package br.com.loja.florescer.form;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoForm(@NotBlank String descricao, @Positive @NotNull BigDecimal preco, @Positive @NotNull int quantidadeEstoque, @NotBlank String filial, @Positive @NotNull long idFornecedor) { }
