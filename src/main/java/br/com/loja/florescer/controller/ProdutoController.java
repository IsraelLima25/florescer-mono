package br.com.loja.florescer.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.loja.florescer.exception.NotFoundException;
import br.com.loja.florescer.form.ProdutoForm;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.repository.FornecedorRepository;
import br.com.loja.florescer.repository.ProdutoRepository;
import br.com.loja.florescer.view.ProdutoView;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
@SecurityRequirement(name = "bearer-key")
public class ProdutoController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProdutoController.class);
	
	private ProdutoRepository produtoRepository;
	private FornecedorRepository fornecedorRepository;
	
	public ProdutoController(ProdutoRepository produtoRepository, FornecedorRepository fornecedorRepository) {
		this.produtoRepository = produtoRepository;
		this.fornecedorRepository=fornecedorRepository;
	}

	@GetMapping
	public ResponseEntity<List<ProdutoView>> listarTodos(){
		LOGGER.info("Buscando produtos");
		List<Produto> produtos = produtoRepository.findAll();
		List<ProdutoView> viewProdutos = produtos.stream()
				.map(produto -> new ProdutoView(produto.getDescricao(), produto.getPreco(), produto.getQuantidadeEstoque(), produto.getFilial()))
				.collect(Collectors.toList());
		LOGGER.info("Fornecedores encontrado");		
		return ResponseEntity.ok(viewProdutos);
	}
	
	// TODO create endpoint list client by unit! Example -> go/ba .....
	
	@PostMapping
	@Transactional
	public ResponseEntity<ProdutoView> cadastrarProduto(@Valid @RequestBody ProdutoForm form){
		LOGGER.info("Buscando fornecedor do produto");
		Optional<Fornecedor> possivelFornecedor = fornecedorRepository.findById(form.idFornecedor());
		if(!possivelFornecedor.isPresent()) {
			LOGGER.warn("Atenção!! Nenhum fornecedor com id {} foi encontrado!", form.idFornecedor());
			throw new NotFoundException("idFornecedor", "Nenhum fornecedor encontrado");
		}
		LOGGER.info("Fornecedor encontrado {} ", possivelFornecedor.get());
		Produto produto = new Produto(form.descricao(), form.preco(), form.quantidadeEstoque(), form.filial(), possivelFornecedor.get());
		LOGGER.info("Cadastrando produto na base de dados ...");
		produtoRepository.save(produto);
		LOGGER.info("produto cadastrado");
		
		ProdutoView viewProduto = new ProdutoView(produto.getDescricao(), produto.getPreco(), produto.getQuantidadeEstoque(), produto.getFilial());
		
		return ResponseEntity.ok(viewProduto);
	}
}
