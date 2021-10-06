package com.bootcamp.dscatalog.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {
	
	@Autowired
	private ProductService service;
	
	@GetMapping
	public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable) {
		Page<ProductDTO> list = service.findAllPaged(pageable);
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
		ProductDTO dto = service.findById(id);
		return ResponseEntity.ok(dto);
	}
	
	@PostMapping
	public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO req) {
		req = service.insert(req);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(req.getId()).toUri();
		return ResponseEntity.created(uri).body(req);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO req) {
		req = service.update(id, req);
		return ResponseEntity.ok(req);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> update(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}


}
