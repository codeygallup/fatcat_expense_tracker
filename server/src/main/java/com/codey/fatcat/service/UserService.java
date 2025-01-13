package com.codey.fatcat.service;

import com.codey.fatcat.entity.User;
import com.codey.fatcat.enums.Role;
import com.codey.fatcat.exception.ResourceNotFoundException;
import com.codey.fatcat.repository.UserRepository;
import com.codey.fatcat.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
    if (!SecurityUtils.hasRole("ADMIN")) {
      throw new AccessDeniedException("User does not have access to this resource");
    }
    return userRepository.findAll();
  }

  public User getUserById(UUID id) {
    SecurityUtils.validateUserAccess(id, userRepository);
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
  }

  public User updateUser(UUID id, User user) {
    SecurityUtils.validateUserAccess(id, userRepository);
    User userToUpdate = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
    userToUpdate.setName(user.getName());
    userToUpdate.setEmail(user.getEmail());
    userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(userToUpdate);
  }

  public User updateUserRole(UUID id, Role newRole) {
    if (!SecurityUtils.hasRole("ADMIN")) {
      throw new AccessDeniedException("Only admins can update user roles");
    }
    User userToUpdate = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));

    userToUpdate.setRole(newRole);
    return userRepository.save(userToUpdate);
  }

  public boolean deleteUser(UUID id) {
    SecurityUtils.validateUserAccess(id, userRepository);
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
