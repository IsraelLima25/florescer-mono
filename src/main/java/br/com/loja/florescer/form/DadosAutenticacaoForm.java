package br.com.loja.florescer.form;

import jakarta.validation.constraints.NotBlank;

public record DadosAutenticacaoForm(@NotBlank String login, @NotBlank String senha) { }
