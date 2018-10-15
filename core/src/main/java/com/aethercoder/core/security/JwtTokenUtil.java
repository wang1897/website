package com.aethercoder.core.security;

import com.aethercoder.core.contants.WalletConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * Created by hepen on 7/12/2017.
 */
@Configuration
public class JwtTokenUtil {

    private String secret = WalletConstants.JWT_SECURITY;

//    private Integer expiration;

    public String generateToken(String username, Long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(generateExpirationDate(expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Date generateExpirationDate(Long expiration) {
        Date date = new Date();
        date.setTime(date.getTime() + expiration);
        return date;
    }

    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {

            claims = null;
        }
        return claims;
    }

    public String getUsernameFromToken(String token) {
        System.out.println("token: " + token);
        Claims claims = this.getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    public boolean validateToken(String token, String user) {
        String nameInToken = getUsernameFromToken(token);
        return nameInToken.equals(user);
    }

    @Bean
    public JwtTokenUtil getThreadBean() {
        return new JwtTokenUtil();
    }
}
