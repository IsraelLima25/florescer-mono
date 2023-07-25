package br.com.loja.florescer.form;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteForm (@NotBlank String nome, @NotBlank String cpf, @NotBlank String rg, @NotNull LocalDate dataNascimento, @Valid @NotNull EnderecoForm enderecoForm) { }
