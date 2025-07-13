package com.codey.fatcat.controller;

import com.codey.fatcat.dto.UserDTO;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.enums.Role;
import com.codey.fatcat.service.CustomUserDetailsService;
import com.codey.fatcat.service.UserService;
import com.codey.fatcat.utils.DTOConverter;
import com.codey.fatcat.webtoken.LoginForm;
import com.codey.fatcat.webtoken.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;

  public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService,
                        CustomUserDetailsService customUserDetailsService) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.customUserDetailsService = customUserDetailsService;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
    userService.registerUser(user);
    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginUser(@Valid @RequestBody LoginForm loginForm) {
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.email(),
                                                                                   loginForm.password()));

    if (authentication.isAuthenticated()) {
      String token = jwtService.generateToken(customUserDetailsService.loadUserByUsername(loginForm.email()));
      return ResponseEntity.ok(token);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    List<UserDTO> userDTOs = users.stream()
        .map(DTOConverter::convertToDTO)
        .toList();
    return ResponseEntity.ok(userDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(DTOConverter.convertToDTO(user));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody User user) {
    User updatedUser = userService.updateUser(id, user);
    return ResponseEntity.accepted().body(DTOConverter.convertToDTO(updatedUser));
  }

  @PatchMapping("/{id}/role")
  public ResponseEntity<UserDTO> updateUserRole(@PathVariable UUID id, @RequestBody Role role) {
    User updatedUser = userService.updateUserRole(id, role);
    return ResponseEntity.accepted().body(DTOConverter.convertToDTO(updatedUser));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    return userService.deleteUser(id) ? ResponseEntity.noContent().build() :
        ResponseEntity.notFound().build();
  }
}
