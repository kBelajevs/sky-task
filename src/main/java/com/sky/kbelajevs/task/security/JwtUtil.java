package com.sky.kbelajevs.task.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME_IN_MILLIS = 86_400_000;
    private final String SECRET_KEY = "Xx0UNmlhlk0uv8LQN3UMSIIL0foH00D9wpK1qyzAZLY=";

    public String createToken(Authentication authentication) {

        var username = authentication.getName();
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        var now = new Date();
        var expiryDate = new Date(now.getTime() + EXPIRATION_TIME_IN_MILLIS);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        var usernameFromToken = getUsernameFromToken(token);
        return (username.equals(usernameFromToken) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Boolean isTokenExpired(String token) {
        var expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        var claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        var parserBuilder = Jwts.parserBuilder();
        parserBuilder.setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()));
        return parserBuilder.build().parseClaimsJws(token).getBody();
    }
}