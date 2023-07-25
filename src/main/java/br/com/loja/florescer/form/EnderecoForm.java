package br.com.loja.florescer.form;

import jakarta.validation.constraints.NotBlank;

public record EnderecoForm(@NotBlank String cep) { }
