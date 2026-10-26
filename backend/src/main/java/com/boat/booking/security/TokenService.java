package com.boat.booking.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Service
public class TokenService {
 private final SecretKey key;
 public TokenService(@Value("${app.jwt.secret}") String secret){if(secret.getBytes(StandardCharsets.UTF_8).length<32)throw new IllegalArgumentException("JWT_SECRET must be at least 32 bytes");this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 public String create(Long userId,String email){return Jwts.builder().subject(email).claim("uid",userId).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+86400000L)).signWith(key).compact();}
 public String emailFrom(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
 public Long userIdFrom(String token){Object id=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("uid");return ((Number)id).longValue();}
}