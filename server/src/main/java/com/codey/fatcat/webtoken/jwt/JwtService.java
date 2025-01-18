package com.codey.fatcat.webtoken.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private static String SECRET;
  //      "88ecab812fbe3505d854069bf1386fc7dd7d8d00075271ebd74322aa7087103f79f57a8554a24a6cd791fc1be9a3bc53889639fc8cf031c8c31f052a9068d5c988ce3d31577475403343bf1835b6e0fd882ffc30dcdc2e68f246ac0722d2ef367627c07a2d88a828a7b7a048b8a88c0a101d02b78e75c6bbf96fdb6e247c99aa7e6439f433ba14290c4b3515ca2a4da68ef3a460045bfd4dbcd72a4b265518bff309a61ccb212232ee247c0c5486759b29888095ac225aebdf191a6fcedc2ccbfef31096aefc3f0eb7fa79fb125adc90f9a6bf9d076920b6d9538b60d73b90aa8b913da64beabe2ff03f9f6b4d04d688b0f219e85da13e362119aaa27d3b78b0";
  @Value("${jwt.expiration}")
  private static long VALIDITY;
//    TimeUnit.MINUTES.toMillis(1000);

  public String generateToken(UserDetails userDetails) {
    Map<String, String> claims = new HashMap<>();
    claims.put("username", userDetails.getUsername());
    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
        .signWith(generateKey())
        .compact();
  }

  private SecretKey generateKey() {
    byte[] decodedKey = Base64.getDecoder().decode(SECRET);
    return Keys.hmacShaKeyFor(decodedKey);
  }

  public String extractUsername(String jwt) {
    Claims claims = getClaims(jwt);
    return claims.getSubject();
  }

  private Claims getClaims(String jwt) {
    return Jwts.parser()
        .verifyWith(generateKey())
        .build()
        .parseSignedClaims(jwt)
        .getPayload();
  }

  public boolean isTokenValid(String jwt) {
    Claims claims = getClaims(jwt);
    return claims.getExpiration().after(Date.from(Instant.now()));
  }
}
