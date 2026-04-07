package com.codey.fatcat.service;

import com.codey.fatcat.dto.RegisterRecord;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.enums.Role;
import com.codey.fatcat.exception.EmailAlreadyInUseException;
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

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public void registerUser(RegisterRecord request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyInUseException("Email is already in use");
    }
    User user = new User();
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);
    userRepository.save(user);
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

  public User updateUser(UUID id, RegisterRecord request) {
    SecurityUtils.validateUserAccess(id, userRepository);
    User userToUpdate = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
    userToUpdate.setEmail(request.email());
    if (request.password() != null && !request.password().trim().isEmpty()) {
      userToUpdate.setPassword(passwordEncoder.encode(request.password().trim()));
    }
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
