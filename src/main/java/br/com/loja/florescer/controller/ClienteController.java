package br.com.loja.florescer.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.loja.florescer.api.ApiEnderecoService;
import br.com.loja.florescer.form.ClienteForm;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.repository.ClienteRepository;
import br.com.loja.florescer.service.ClienteService;
import br.com.loja.florescer.util.ClienteConverter;
import br.com.loja.florescer.view.ClienteView;
import br.com.loja.florescer.view.EnderecoView;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@SecurityRequirement(name = "bearer-key")
public class ClienteController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClienteController.class);

	private ClienteRepository clienteRepository;
	private ApiEnderecoService apiEnderecoService;
	private ClienteConverter clienteConverter;
	private ClienteService clienteService;
	
	public ClienteController(ClienteRepository clienteRepository, ApiEnderecoService apiEnderecoService, 
			ClienteConverter clienteConverter, ClienteService clienteService) {
		this.clienteRepository=clienteRepository;
		this.apiEnderecoService=apiEnderecoService;
		this.clienteConverter=clienteConverter;
		this.clienteService=clienteService;
	}
	
	@GetMapping
	public ResponseEntity<List<ClienteView>> listarTodos() {
		LOGGER.info("Buscando clientes");
		List<Cliente> clientes = clienteRepository.findAll();
		LOGGER.info("Clientes encontrado");
		List<ClienteView> clientesView = clientes.stream().map(cliente ->
		clienteConverter.toClienteView(cliente)).collect(Collectors.toList());
		
		return ResponseEntity.ok(clientesView);
	}
	
	@GetMapping("/{cpf}")
	public ResponseEntity<ClienteView> buscarPorCpf(@PathVariable("cpf") String cpf){
		LOGGER.info("Buscando cliente por cpf {} ", cpf);
		Optional<Cliente> possivelCliente = clienteService.buscarClientePorCpf(cpf);
		if(!possivelCliente.isPresent()) {
			LOGGER.warn("Atenção!! Nenhum cliente com cpf {} foi encontrado!", cpf);
			return ResponseEntity.notFound().build();
		}
		Cliente cliente = possivelCliente.get();
		LOGGER.info("Cliente encontrado {} ", cliente);
		ClienteView clienteView = clienteConverter.toClienteView(cliente);
		
		return ResponseEntity.ok(clienteView);
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<ClienteView> cadastrarCliente(@Valid @RequestBody ClienteForm form){
		LOGGER.info("Consultando endereco");
		EnderecoView enderecoView = apiEnderecoService.requestEnderecoJSON(form.enderecoForm().cep());
		LOGGER.info("Endereco capturado {}", enderecoView);
		Endereco endereco = new Endereco(enderecoView.cep(), enderecoView.logradouro(), enderecoView.complemento(), enderecoView.bairro(), enderecoView.localidade(), enderecoView.uf());
		Cliente cliente = new Cliente(form.nome(), form.cpf(), form.rg(), form.dataNascimento(), endereco);
		LOGGER.info("Cadastrando cliente na base de dados ...");
		clienteRepository.save(cliente);
		LOGGER.info("Cliente cadastrado");
		ClienteView clienteView = clienteConverter.toClienteView(cliente);
		
		return ResponseEntity.ok(clienteView);
	}
	
	@PutMapping("/{cpf}")
	@Transactional
	public ResponseEntity<ClienteView> atualizarCliente(@Valid @RequestBody ClienteForm form, @PathVariable("cpf") String cpf){
		LOGGER.info("Buscando cliente por cpf {} ", cpf);
		Optional<Cliente> possivelCliente = clienteRepository.findByCpf(cpf);
		if(!possivelCliente.isPresent()) {
			LOGGER.warn("Não existe cpf cadastrado com o numero {} ", cpf);
			return ResponseEntity.notFound().build();
		}
		Cliente cliente = possivelCliente.get();
		
		LOGGER.info("Consultando endereco");
		EnderecoView enderecoView = apiEnderecoService.requestEnderecoJSON(form.enderecoForm().cep());
		LOGGER.info("Endereco capturado {}", enderecoView);
		
		LOGGER.info("Cliente encontrado {} ", cliente);
		cliente.atualizarCliente(form.nome(), form.cpf(), form.rg(), form.dataNascimento(), 
				new Endereco(cliente.getEndereco().getId(), enderecoView.cep(), enderecoView.logradouro(), enderecoView.complemento(), enderecoView.bairro(), enderecoView.localidade(), enderecoView.uf()));
		clienteRepository.save(cliente);
		LOGGER.info("Cliente atualizado");
		
		ClienteView clienteView = clienteConverter.toClienteView(cliente);
		
		return ResponseEntity.ok(clienteView);
	}

}
