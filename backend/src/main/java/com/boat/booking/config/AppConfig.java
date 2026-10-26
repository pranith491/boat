package com.boat.booking.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class AppConfig {
 @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
 @Bean WebMvcConfigurer corsConfigurer(@Value("${app.frontend-origin}") String origin) {
  return new WebMvcConfigurer(){ @Override public void addCorsMappings(CorsRegistry registry){
   registry.addMapping("/api/**").allowedOrigins(origin,"http://localhost:5500","http://127.0.0.1:5500")
   .allowedMethods("GET","POST","OPTIONS").allowedHeaders("*"); }};
 }
}