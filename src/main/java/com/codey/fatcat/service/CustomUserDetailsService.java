package com.codey.fatcat.service;

import com.codey.fatcat.entity.User;
import com.codey.fatcat.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    System.out.println("EMAIL: " + email);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    System.out.println("USER: " + user);

    String authority = "ROLE_" + user.getRole();
    System.out.println("Authority: " + authority);

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .authorities(authority)
        .build();

  }
}
