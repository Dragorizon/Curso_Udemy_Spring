package com.bolsadeideas.springboot.app.apicontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;

@RestController
@RequestMapping("/api")
public class ClienteControllerApi {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes/{id}")
	public Cliente listar(@PathVariable(value = "id") Long id) {
		return clienteService.findOne(id);
	}

}
