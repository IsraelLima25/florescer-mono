package br.com.loja.florescer.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FornecedorForm(@NotBlank String nome, @NotBlank String cnpj, @Valid @NotNull EnderecoForm enderecoForm) { }
