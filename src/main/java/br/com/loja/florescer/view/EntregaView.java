package br.com.loja.florescer.view;

import br.com.loja.florescer.indicador.AvaliacaoIndicador;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;

public record EntregaView(long idEntrega, StatusEntregaIndicador status, AvaliacaoIndicador avaliacao) { }
