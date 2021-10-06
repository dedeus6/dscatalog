package com.bootcamp.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.services.ProductService;
import com.bootcamp.dscatalog.services.exceptions.DataBaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	
	@BeforeEach
	void setUp() throws Exception {
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(existingId)).thenReturn(productDTO);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
		Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DataBaseException.class).when(service).delete(dependentId);
		
		Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(productDTO);
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExist() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void insertShouldReturnProductDTOWhenCreatedProduct() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(post("/products", productDTO)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));
	
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void deleteShouldReturnNoContetWhenIdExist() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", existingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", nonExistingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}

}
