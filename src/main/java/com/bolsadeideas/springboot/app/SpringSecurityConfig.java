package com.bolsadeideas.springboot.app;

import com.bolsadeideas.springboot.app.auth.filter.JWTAuthenticationFilter;
import com.bolsadeideas.springboot.app.auth.filter.JWTAuthorizationFilter;
//import com.bolsadeideas.springboot.app.auth.hanlder.LoginSuccesHandler;
import com.bolsadeideas.springboot.app.auth.service.JWTService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
 
import com.bolsadeideas.springboot.app.models.service.JpaUserDetailsService;


@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig {
   
//   @Autowired
//   private LoginSuccesHandler sucessHandler;
   
   @Autowired
   private BCryptPasswordEncoder passwordEncoder;
 
   @Autowired
   private JpaUserDetailsService userDetailService;
   
   @Autowired
   private AuthenticationConfiguration authenticationConfiguration;
   
   @Autowired
   private JWTService jwtService;
  
   @Bean
   public AuthenticationManager authenticationManager() throws Exception {
       return authenticationConfiguration.getAuthenticationManager();
   }

   @Autowired
   public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
      build.userDetailsService(userDetailService)
      .passwordEncoder(passwordEncoder);
   }
 
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
               .authorizeHttpRequests()
               .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar/**", "/locale", "/clientes/**")
               .permitAll()
               .anyRequest().authenticated()
               .and()
               .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService))
               .addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtService))
               .csrf(config -> config.disable())
               .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	   
//       .authorizeHttpRequests()
//       .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar","/locale", "/api**", "/clientes")
//       .permitAll()
//       .anyRequest().authenticated()
//       .and()
//       .addFilter(new JWTAuthenticationFilter(authenticationManager()))
//       .csrf().disable()
//       .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
 
	   
	   return http.build();
    }
}
