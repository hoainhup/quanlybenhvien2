package com.example.quanlybenhvien.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(1)
public class AdminSecurityConfig {
    private final PasswordEncoder passwordEncoder;

    public AdminSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/quanly/**","/api/**","/js/**") // Chỉ áp dụng cho "/admin/**"
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/quanly/**","/api/**").hasRole("VT00") // Chỉ ADMIN mới vào được
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/quanly/login") // Trang đăng nhập riêng cho admin
                .loginProcessingUrl("/quanly/login")
                .defaultSuccessUrl("/quanly/trangchu", true) // Sau khi đăng nhập thành công chuyển vào "/admin/home"
                .failureUrl("/quanly/login?error=true")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/quanly/logout") // Đăng xuất dành riêng cho admin
                .logoutSuccessUrl("/quanly/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true))
            .csrf(csrf -> csrf.disable());
    
        return http.build();
    }
  
}
