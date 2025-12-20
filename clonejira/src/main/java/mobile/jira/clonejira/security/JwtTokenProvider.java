package mobile.jira.clonejira.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    @Value("${jwt.jwtSecret}")
    private String jwtSecret;

    @Value("${jwt.jwtExpiration}")
    private int jwtExpiration;


    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(Authentication auth) {
        String uid = auth.getName();

        Date currentDate = new Date();
        Date expirDate = new Date(currentDate.getTime() + jwtExpiration);

        String token = Jwts.builder()
                        .setSubject(uid)
                        .setIssuedAt(currentDate)
                        .setExpiration(expirDate)
                        .signWith(key(), SignatureAlgorithm.HS256)
                        .compact();
        return token;
    }

    public String getUid(String token) {
        Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) throws Unauthorized {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parse(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
