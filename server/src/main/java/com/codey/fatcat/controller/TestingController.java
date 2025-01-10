package com.codey.fatcat.controller;

import com.codey.fatcat.service.CustomUserDetailsService;
import com.codey.fatcat.webtoken.JwtService;
import com.codey.fatcat.webtoken.LoginForm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestingController {

  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;
  private AuthenticationManager authenticationManager;

  public TestingController(JwtService jwtService,
                           CustomUserDetailsService customUserDetailsService,
                           AuthenticationManager authenticationManager) {
    this.jwtService = jwtService;
    this.customUserDetailsService = customUserDetailsService;
    this.authenticationManager = authenticationManager;
  }


  @GetMapping("/home")
  public String test() {
    return "home";
  }

  @PostMapping("/authenticate")
  public String authenticatdAndGetToken(@RequestBody LoginForm loginForm) {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        loginForm.email(), loginForm.password()
    ));
    if (authentication.isAuthenticated()) {
      return jwtService.generateToken(customUserDetailsService.loadUserByUsername(loginForm.email()));
    } else {
      throw new UsernameNotFoundException("Invalid email or password");
    }
  }
}
