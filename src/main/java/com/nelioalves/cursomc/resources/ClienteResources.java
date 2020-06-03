package com.nelioalves.cursomc.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nelioalves.cursomc.domain.Cliente;
import com.nelioalves.cursomc.domain.ClienteNewDTO;
import com.nelioalves.cursomc.dto.ClienteDTO;
import com.nelioalves.cursomc.services.ClienteService;

@RestController
@RequestMapping(value="/clientes")
public class ClienteResources {
	
	@Autowired
	ClienteService service;

	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ResponseEntity<Cliente> find(@PathVariable Integer id) {
		
		Cliente obj = service.find(id);
		return ResponseEntity.ok().body(obj);
	}

	@RequestMapping(value="/email", method = RequestMethod.GET)
	public ResponseEntity<Cliente> find(@RequestParam(value="value") String email) {
		
		Cliente obj = service.findByEmail(email);
		return ResponseEntity.ok().body(obj);
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@Valid @RequestBody ClienteDTO objDTO, @PathVariable Integer id){
		Cliente obj = service.fromDTO(objDTO);
		obj.setId(id);
		service.update(obj);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<List<ClienteDTO>> findAll() {
		
		List<Cliente> list = service.findAll();
		List<ClienteDTO> listDTO = list.stream().map(categoria -> new ClienteDTO(categoria)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDTO);
	}

	@RequestMapping(value="/page", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Page<ClienteDTO>> findPage(@RequestParam(value="page", defaultValue = "0") Integer page
													 , @RequestParam(value="linesPerPage", defaultValue = "24") Integer linesPerPage
													 , @RequestParam(value="direction", defaultValue = "ASC") String direction
													 , @RequestParam(value="orderBy", defaultValue = "nome") String orderBy) {
		
		Page<Cliente> list = service.findPage(page, linesPerPage, direction, orderBy);
		Page<ClienteDTO> listDTO = list.map(categoria -> new ClienteDTO(categoria));
		return ResponseEntity.ok().body(listDTO);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody ClienteNewDTO objDTO){
		Cliente obj = service.fromDTO(objDTO);
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@RequestMapping(value="/picture", method = RequestMethod.POST)
	public ResponseEntity<Void> uploadProfilePicture(@RequestParam(name="file") MultipartFile multipartFile){
		URI uri = service.uploadProfilePicture(multipartFile);
		return ResponseEntity.created(uri).build();
	}

}
