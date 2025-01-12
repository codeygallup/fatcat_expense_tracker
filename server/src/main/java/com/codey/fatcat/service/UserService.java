package com.codey.fatcat.service;

import com.codey.fatcat.entity.User;
import com.codey.fatcat.enums.Role;
import com.codey.fatcat.exception.ResourceNotFoundException;
import com.codey.fatcat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public void registerUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  public void loginUser(User user) {
    System.out.println("Logging in");

  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
  }

  public User updateUser(UUID id, User user) {
    User userToUpdate = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
    userToUpdate.setName(user.getName());
    userToUpdate.setEmail(user.getEmail());
    userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(userToUpdate);
  }

  public User updateUserRole(UUID id, Role newRole) {
//    if (!SecurityUtils.hasRole("ADMIN")) {
//      throw new AccessDeniedException("Only admins can update user roles");
//    }
    User userToUpdate = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));

    userToUpdate.setRole(newRole);
    return userRepository.save(userToUpdate);
  }

  public boolean deleteUser(UUID id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
