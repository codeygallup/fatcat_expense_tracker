package com.codey.fatcat.service;

import com.codey.fatcat.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;

  public UserService(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public void saveUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

}
