package com.boat.booking.controller;
import com.boat.booking.model.Booking;
import com.boat.booking.model.User;
import com.boat.booking.repository.BookingRepository;
import com.boat.booking.repository.UserRepository;
import com.boat.booking.security.TokenService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api")
public class BookingController {
 private static final BigDecimal UNIT_PRICE=new BigDecimal("1499.00");
 private final BookingRepository bookings; private final UserRepository users; private final TokenService tokens;
 public BookingController(BookingRepository bookings,UserRepository users,TokenService tokens){this.bookings=bookings;this.users=users;this.tokens=tokens;}
 @PostMapping("/bookings") public ResponseEntity<?> create(@RequestHeader(value="Authorization",required=false) String authorization,@RequestBody Map<String,Object> body){
  User user=authenticatedUser(authorization);if(user==null)return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please log in again."));
  try{
   int quantity=Integer.parseInt(String.valueOf(body.get("quantity")));if(quantity<1||quantity>10)return ResponseEntity.badRequest().body(Map.of("message","Quantity must be between 1 and 10."));
   LocalDate date=LocalDate.parse(String.valueOf(body.get("bookingDate")));if(date.isBefore(LocalDate.now()))return ResponseEntity.badRequest().body(Map.of("message","Booking date cannot be in the past."));
   String phone=String.valueOf(body.getOrDefault("phone","")).trim();if(!phone.matches("[0-9]{10}"))return ResponseEntity.badRequest().body(Map.of("message","Enter a valid 10-digit phone number."));
   Booking b=bookings.save(new Booking(user,"boAt Wireless Bluetooth",quantity,date,phone,UNIT_PRICE.multiply(BigDecimal.valueOf(quantity))));
   return ResponseEntity.ok(toMap(b));
  }catch(Exception ex){return ResponseEntity.badRequest().body(Map.of("message","Please check the quantity, date, and phone number."));}
 }
 @GetMapping("/bookings/my") public ResponseEntity<?> mine(@RequestHeader(value="Authorization",required=false) String authorization){
  User user=authenticatedUser(authorization);if(user==null)return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please log in again."));
  return ResponseEntity.ok(bookings.findByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(this::toMap).toList());
 }
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok","service","boat-booking-api");}
 private User authenticatedUser(String auth){try{if(auth==null||!auth.startsWith("Bearer "))return null;String t=auth.substring(7);String email=tokens.emailFrom(t);Long id=tokens.userIdFrom(t);return users.findById(id).filter(u->u.getEmail().equalsIgnoreCase(email)).orElse(null);}catch(JwtException|IllegalArgumentException ex){return null;}}
 private Map<String,Object> toMap(Booking b){return Map.of("id",b.getId(),"productName",b.getProductName(),"quantity",b.getQuantity(),"bookingDate",b.getBookingDate().toString(),"phone",b.getPhone(),"totalPrice",b.getTotalPrice());}
}