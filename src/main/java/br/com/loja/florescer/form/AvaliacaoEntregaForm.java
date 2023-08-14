package br.com.loja.florescer.form;

import br.com.loja.florescer.indicador.AvaliacaoIndicador;
import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AvaliacaoEntregaForm(@NonNull @Positive long idPedido, @NotNull AvaliacaoIndicador avaliacao) { }
