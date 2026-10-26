package com.boat.booking.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="bookings")
public class Booking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="user_id") private User user;
 @Column(nullable=false) private String productName;
 @Column(nullable=false) private int quantity;
 @Column(nullable=false) private LocalDate bookingDate;
 @Column(nullable=false,length=10) private String phone;
 @Column(nullable=false,precision=10,scale=2) private BigDecimal totalPrice;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 protected Booking(){}
 public Booking(User user,String productName,int quantity,LocalDate bookingDate,String phone,BigDecimal totalPrice){this.user=user;this.productName=productName;this.quantity=quantity;this.bookingDate=bookingDate;this.phone=phone;this.totalPrice=totalPrice;}
 public Long getId(){return id;} public String getProductName(){return productName;} public int getQuantity(){return quantity;} public LocalDate getBookingDate(){return bookingDate;} public String getPhone(){return phone;} public BigDecimal getTotalPrice(){return totalPrice;}
}