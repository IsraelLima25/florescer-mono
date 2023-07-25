package br.com.loja.florescer.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.loja.florescer.api.ApiEnderecoService;
import br.com.loja.florescer.form.FornecedorForm;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.repository.FornecedorRepository;
import br.com.loja.florescer.view.EnderecoView;
import br.com.loja.florescer.view.FornecedorView;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FornecedorController.class);
	
	private FornecedorRepository fornecedorRespository;

	private ApiEnderecoService apiEnderecoService;
	
	public FornecedorController(FornecedorRepository fornecedorRepository, ApiEnderecoService apiEnderecoService) {
		this.fornecedorRespository = fornecedorRepository;
		this.apiEnderecoService = apiEnderecoService;
	}

	@GetMapping
	public ResponseEntity<List<FornecedorView>> listarTodos(){
		LOGGER.info("Buscando fornecedores");
		List<Fornecedor> fornecedores = fornecedorRespository.findAll();
		List<FornecedorView> viewFornecedores = fornecedores.stream()
				.map((fornecedor) -> new FornecedorView(fornecedor.getNome(), fornecedor.getCnpj()))
				.collect(Collectors.toList());
		LOGGER.info("Fornecedores encontrado");
		return ResponseEntity.ok(viewFornecedores);
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<FornecedorView> cadastrar(@Valid @RequestBody FornecedorForm form){
		LOGGER.info("Consultando endereco");
		EnderecoView enderecoView = apiEnderecoService.requestEnderecoJSON(form.enderecoForm().cep());
		LOGGER.info("Endereco capturado {}", enderecoView);
		Endereco endereco = new Endereco(enderecoView.cep(), enderecoView.logradouro(), enderecoView.complemento(), enderecoView.bairro(), enderecoView.localidade(), enderecoView.uf());
		Fornecedor fornecedor = new Fornecedor(form.nome(), form.cnpj(), endereco);
		LOGGER.info("Cadastrando fornecedor na base de dados ...");
		fornecedorRespository.save(fornecedor);
		LOGGER.info("Fornecedor cadastrado");
		FornecedorView viewFornecedor = new FornecedorView(fornecedor.getNome(), fornecedor.getCnpj());
		return ResponseEntity.ok(viewFornecedor);
	}

}
