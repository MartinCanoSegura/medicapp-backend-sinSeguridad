package com.mitocode.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.exception.ModeloNotFoundException;
import com.mitocode.model.Paciente;
import com.mitocode.model.Pedido;
import com.mitocode.service.IPacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
	
	Logger logger = LoggerFactory.getLogger(PacienteController.class);

	@Autowired
	private IPacienteService service;
	
	@GetMapping
	public ResponseEntity<List<Paciente>> listar() throws Exception {		
		List<Paciente> lista = service.listar();
		return new ResponseEntity<List<Paciente>>(lista, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Paciente> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Paciente obj = service.listarPorId(id);		
		
		if(obj == null) {
			throw new ModeloNotFoundException("PACIENTE CON ID = " + id + " NO ENCONTRADO ");
		}
		return new ResponseEntity<Paciente>(obj, HttpStatus.OK);
	}
	
	@GetMapping("/hateoas/{id}")
	public EntityModel<Paciente> listarPorIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Paciente obj = service.listarPorId(id);		
		
		if(obj.getIdPaciente() == null) {
			throw new ModeloNotFoundException("PACIENTE CON ID = " + id + " NO ENCONTRADO ");
		}
		//localhost:8080//pacientes/{id}
		EntityModel<Paciente> recurso = EntityModel.of(obj);
		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).listarPorId(id));
		recurso.add(linkTo.withRel("paciente-recurso"));
		return recurso;
	}
	
	/*@PostMapping
	public ResponseEntity<Paciente> registrar(@Valid @RequestBody Paciente p) {
		Paciente obj = service.registrar(p);
		return new ResponseEntity<Paciente>(obj, HttpStatus.CREATED); 
	}*/
	
	@PostMapping
	public ResponseEntity<Paciente> registrar(@Valid @RequestBody Paciente p) throws Exception {
		Paciente obj = service.registrar(p);
		
		//localhost:8080/pacientes/2
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdPaciente()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping
	public ResponseEntity<Paciente> modificar(@Valid @RequestBody Paciente p) throws Exception {
		Paciente obj = service.modificar(p);
		return new ResponseEntity<Paciente>(obj, HttpStatus.OK); 
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {
		Paciente obj = service.listarPorId(id);
		
		if(obj.getIdPaciente() == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		service.eliminar(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	
	@GetMapping("/pageable")
	public ResponseEntity<Page<Paciente>> listarPageable(Pageable pageable) throws Exception {
		Page<Paciente> pacientes = service.listarPageable(pageable);
		return new ResponseEntity<Page<Paciente>>(pacientes, HttpStatus.OK);
	}
	
	
	
	
	@GetMapping("/msg")
	public String mensaje(){
		return service.mensaje();
	}
	
	@PostMapping("/comparaArray")
	public Pedido comparador(@RequestBody Pedido p) {
//		public Map<String, Long> comparador(@RequestBody Pedido p) {
//			Map<String, Long> obj = service.comparaArryList(p);
		return service.comparaArryList(p);
		 
	}

	/**
	 --- FORMATO JSON EN POSTMAN ---
	 POST: http://localhost:8080/pacientes/comparaArray
	 
	{
	"listA" : ["POLLO","AGUACATE","TOMATE","CELULAR","MOCHILA","PC","BOCINA","MOCHILA","POLLO","HUEVO","TOMATE"],
	"listB" : ["TOMATE","MOCHILA"]
	}
	--- SALIDA POR CONSOLA DE ECLIPSE ---

	[TOMATE, MOCHILA, MOCHILA, TOMATE]
	{TOMATE=2, MOCHILA=2}
	*/
	
}
