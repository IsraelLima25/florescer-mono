package br.com.loja.florescer.form;

import jakarta.validation.constraints.NotBlank;

public record EnderecoEntregaForm(@NotBlank String cep) { }
