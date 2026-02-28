package uz.coder.davomatbackend.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    @Value("${jwt.secret:my-super-secret-key-my-super-secret-key-my-super-secret-key-change-in-production}")
    private String secretKey;

    @Value("${jwt.expiration:2592000000}")
    private long expirationTime; // 30 days in milliseconds

    // 🔑 TOKEN YARATISH
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 👤 USERNAME O'QISH
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ TOKEN TEKSHIRISH
    public boolean isTokenValid(String token, UserDetails userDetails, Instant lastPasswordResetAt) {
        String username = extractUsername(token);

        Date issuedAt = extractAllClaims(token).getIssuedAt();

        boolean tokenIssuedBeforePasswordChange =
                issuedAt.toInstant().isBefore(lastPasswordResetAt);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !tokenIssuedBeforePasswordChange;
    }

    // ⏰ MUDDAT TEKSHIRISH
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // 📦 CLAIMS
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔐 SIGNING KEY
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}
