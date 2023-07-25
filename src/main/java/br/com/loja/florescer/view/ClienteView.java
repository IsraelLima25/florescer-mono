package br.com.loja.florescer.view;

import java.time.LocalDate;

public record ClienteView(String nome, String cpf, String rg, LocalDate dataNascimento, EnderecoView endereco) { }
