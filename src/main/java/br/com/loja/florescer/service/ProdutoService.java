package br.com.loja.florescer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.loja.florescer.model.Produto;

@Service
public class ProdutoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProdutoService.class);
	
	public void abaterEstoque(Produto produto, Integer quantidade) {
		LOGGER.info("Fazendo abatimento no estoque...");
		produto.abater(quantidade);
		LOGGER.info("Estoque abatido");
	}
	
}
