package com.boat.booking.controller;
import com.boat.booking.model.User;
import com.boat.booking.repository.UserRepository;
import com.boat.booking.security.TokenService;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.regex.Pattern;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder; private final TokenService tokens;
 public AuthController(UserRepository users,PasswordEncoder encoder,TokenService tokens){this.users=users;this.encoder=encoder;this.tokens=tokens;}
 @PostMapping("/register") public ResponseEntity<?> register(@RequestBody Map<String,String> body){
  String name=clean(body.get("name")),email=clean(body.get("email")).toLowerCase(),password=body.getOrDefault("password","");
  if(name.isBlank()||email.isBlank()||!email.contains("@")||password.length()<6)return ResponseEntity.badRequest().body(Map.of("message","Enter a name, valid email, and password with at least 6 characters."));
  if(users.existsByEmailIgnoreCase(email))return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","An account with this email already exists."));
  User user=users.save(new User(name,email,encoder.encode(password)));return ResponseEntity.ok(response(user));
 }
 @PostMapping("/login") public ResponseEntity<?> login(@RequestBody Map<String,String> body){
  String email=clean(body.get("email")).toLowerCase(),password=body.getOrDefault("password","");
  User user=users.findByEmailIgnoreCase(email).orElse(null);
  if(user==null||!encoder.matches(password,user.getPasswordHash()))return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Incorrect email or password."));
  return ResponseEntity.ok(response(user));
 }
 private Map<String,Object> response(User u){return Map.of("token",tokens.create(u.getId(),u.getEmail()),"userId",u.getId(),"name",u.getName(),"email",u.getEmail());}
 private String clean(String s){return s==null?"":s.trim();}
}