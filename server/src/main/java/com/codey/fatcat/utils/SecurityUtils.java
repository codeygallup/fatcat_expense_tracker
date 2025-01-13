package com.codey.fatcat.utils;

import com.codey.fatcat.entity.Account;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.exception.ResourceNotFoundException;
import com.codey.fatcat.exception.UnauthorizedException;
import com.codey.fatcat.repository.AccountRepository;
import com.codey.fatcat.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

  public static String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UnauthorizedException("User is not authenticated");
    }
    return authentication.getName();
  }

  public static boolean hasRole(String role) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.getAuthorities()
        .stream()
        .anyMatch(g -> g.getAuthority().equals("ROLE_ " + role));
  }

  public static void validateUserAccess(UUID userId, UserRepository userRepository) {
    String userEmail = getCurrentUserEmail();
    User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UnauthorizedException("User not found"));
    if (!user.getId().equals(userId) && !hasRole("ADMIN")) {
      throw new AccessDeniedException("User does not have access to this resource");
    }
  }

  public static void validateAccountAccess(UUID accountId, AccountRepository accountRepository) {
    String userEmail = getCurrentUserEmail();
    Account account =
        accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    if (!account.getUser().getEmail().equals(userEmail) && !hasRole("ADMIN")) {
      throw new AccessDeniedException("User does not have access to this resource");
    }
  }
}
