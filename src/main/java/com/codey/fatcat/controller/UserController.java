package com.codey.fatcat.controller;

import com.codey.fatcat.dto.UserDTO;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody User user) {
    userService.registerUser(user);
    return ResponseEntity.ok("User registered successfully");
//    return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginUser(@RequestBody User user) {
    userService.loginUser(user);
    return ResponseEntity.ok("User logged in successfully");
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
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
    return userService.deleteUser(id) ? ResponseEntity.ok("User deleted successfully") :
        ResponseEntity.notFound().build();
  }
}
