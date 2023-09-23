package com.bolsadeideas.springboot.app.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bolsadeideas.springboot.app.auth.service.JWTService;
import com.bolsadeideas.springboot.app.models.entity.Usuario;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private AuthenticationManager authenticationManager;
	
	private JWTService jwtService;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}
	
	



	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String token =  jwtService.create(authResult);
		
		response.addHeader("Authorization", "Bearer " + token);
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", token);
		body.put("user", (User) authResult.getPrincipal());
		body.put("mensaje", String.format("Hola, %shas iniciado sesion con exito!", ((User) authResult.getPrincipal()).getUsername()));
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
		
		
	}


	


	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mensaje", "Error de autenticacion: username o password incorrectos");
		body.put("error",failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}





	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String username = obtainUsername(request);
//		username = (username != null) ? username.trim() : "";

		
		String password = obtainPassword(request);
		
		if(username != null & password != null) {
			logger.info("Username desde request parameter (form-data): " + username);
			logger.info("Password desde request parameter (form-data): " + password);
		}else {
			Usuario user = null;
			try {
				 user = new ObjectMapper().readValue(request.getInputStream(),Usuario.class);
				 
				 username = user.getUsername();
				 password = user.getPassword();
				 
					logger.info("Username desde request InputStream (raw): " + username);
					logger.info("Password desde request InputStream (raw): " + password);
			} catch (StreamReadException e) {
				e.printStackTrace();
			} catch (DatabindException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		username = username.trim();
		
//		String contentType = request.getContentType();
//
//	    if (contentType != null && contentType.contains("application/json")) {
//	        // Si el Content-Type es JSON, intenta analizar el cuerpo JSON de la solicitud
//	        try {
//	            JsonNode jsonNode = new ObjectMapper().readTree(request.getInputStream());
//	            username = jsonNode.get("username").asText();
//	            password = jsonNode.get("password").asText();
//	        } catch (IOException e) {
//	            throw new BadCredentialsException("Error al procesar la solicitud JSON");
//	        }
//	    }
		
		UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.unauthenticated(username,
				password);
		
		return authenticationManager.authenticate(authToken);
	}
	
}
