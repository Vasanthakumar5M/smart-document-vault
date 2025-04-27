package com.app.smart_document_vault.util;

import com.app.smart_document_vault.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    @Value("${secret.key}")
    private String secretKey;

    @Value("${expiration.time}")
    private long jwtExpirationMs;

    public Key getKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user){
        Map<String,Object> map=new HashMap<>();
        return Jwts
                .builder()
                .setSubject(user.getEmail())
                .claim("role",user.getRole().name())
                .setIssuer("Vasanth")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token){
        Claims claims=getClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user){
        String username=extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        Date exptime=getClaims(token).getExpiration();
        return exptime.before(new Date());
    }

    private Claims getClaims(String token){
        return (Claims) Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parse(token)
                .getBody();

    }
}
