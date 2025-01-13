package com.codey.fatcat.controller;

import com.codey.fatcat.dto.UserDTO;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.enums.Role;
import com.codey.fatcat.service.CustomUserDetailsService;
import com.codey.fatcat.service.UserService;
import com.codey.fatcat.webtoken.JwtService;
import com.codey.fatcat.webtoken.LoginForm;
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
  public ResponseEntity<String> registerUser(@RequestBody User user) {
    userService.registerUser(user);
    return ResponseEntity.ok("User registered successfully");
//    return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginUser(@RequestBody LoginForm loginForm) {
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
//    return ResponseEntity.ok(userService.getAllUsers());
    List<User> users = userService.getAllUsers();
    List<UserDTO> userDTOs = users.stream()
        .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getAccounts()))
        .toList();
    return ResponseEntity.ok(userDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getAccounts()));
//    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User user) {
    return new ResponseEntity<>(userService.updateUser(id, user), HttpStatus.ACCEPTED);
  }

  @PatchMapping("/{id}/role")
  public ResponseEntity<User> updateUserRole(@PathVariable UUID id, @RequestBody Role role) {
    User updatedUser = userService.updateUserRole(id, role);
    return new ResponseEntity<>(updatedUser, HttpStatus.ACCEPTED);

  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
    return userService.deleteUser(id) ? ResponseEntity.ok("User deleted successfully") :
        ResponseEntity.notFound().build();
  }
}
