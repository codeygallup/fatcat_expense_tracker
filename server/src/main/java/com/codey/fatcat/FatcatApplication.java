package com.codey.fatcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FatcatApplication {

  public static void main(String[] args) {
//    User user = new User("Codey", "test@test.com", "password");
//    System.out.println("User: " + user);

    SpringApplication.run(FatcatApplication.class, args);
  }

}
