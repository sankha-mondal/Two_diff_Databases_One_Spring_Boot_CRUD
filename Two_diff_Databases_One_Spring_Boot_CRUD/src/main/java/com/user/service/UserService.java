package com.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.entity.User;
import com.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional("userTransactionManager")
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User create(User u) {
		return userRepository.save(u);
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User update(Long id, User updated) {
		return userRepository.findById(id).map(u -> {
			u.setName(updated.getName());
			u.setEmail(updated.getEmail());
			return userRepository.save(u);
		}).orElseThrow(() -> new RuntimeException("User not found"));
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}
}
