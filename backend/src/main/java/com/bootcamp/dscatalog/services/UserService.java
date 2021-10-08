package com.bootcamp.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.dto.RoleDTO;
import com.bootcamp.dscatalog.dto.UserDTO;
import com.bootcamp.dscatalog.dto.UserInsertDTO;
import com.bootcamp.dscatalog.entities.Role;
import com.bootcamp.dscatalog.entities.User;
import com.bootcamp.dscatalog.repositories.RoleRepository;
import com.bootcamp.dscatalog.repositories.UserRepository;
import com.bootcamp.dscatalog.services.exceptions.DataBaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repository.findAll(pageable);
		
		return list.map(item -> new UserDTO(item));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO req) {
		User entity = new User();
		copyDtoToEntity(req, entity);
		entity.setPassword(passwordEncoder.encode(req.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserDTO req) {
		try {
			User entity = repository.getOne(id);
			copyDtoToEntity(req, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);	
		} catch (javax.persistence.EntityNotFoundException e){
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integraty violation");
		}
	}
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		entity.getRoles().clear();
		for (RoleDTO catDto: dto.getRoles()) {
			Role role = roleRepository.getOne(catDto.getId());
			entity.getRoles().add(role);
		}
	}

}
