package com.github.elgleidson.demo.security.security;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.github.elgleidson.demo.security.domain.Role;
import com.github.elgleidson.demo.security.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {
	
	private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
	
	@Value("${app.security.authentication.jwt.secret-key}")
	private String jwtSecretKey;
	
	@Value("${app.security.authentication.jwt.expiration-in-seconds:86400}")
	private long jwtExpirationInSeconds;
	
	public String generateToken(Authentication authentication) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + (jwtExpirationInSeconds * 1000));
		
		User usuario = (User) authentication.getPrincipal();
		String perfis = authentication.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.joining(","));
		
		return Jwts.builder()
			.setSubject(Long.toString(usuario.getId()))
			.claim("id", usuario.getId())
			.claim("username", usuario.getUsername())
			.claim("email", usuario.getEmail())
			.claim("roles", perfis)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(SignatureAlgorithm.HS512, jwtSecretKey)
			.compact();
	}
	
	public Authentication getAuthentication(String authToken) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(authToken).getBody();
		User usuario = new User();
		usuario.setId(Long.parseLong(claims.getSubject()));
		usuario.setUsername(claims.get("username").toString());
		usuario.setEmail(claims.get("email").toString());
		List<Role> perfis = Arrays.stream(claims.get("roles").toString().split(",")).map(Role::new).collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(usuario, authToken, perfis);
	}
	
	public boolean validateToken(String authToken) {
//		Jwts.parser().setSigningKey(Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8)));
		try {
			Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature!");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token!");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token!");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token!");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty or null!");
		}
		
		return false;
	}

}
