


package tn.esprit.adminservice.util;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tn.esprit.adminservice.entity.Profile;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    private javax.crypto.SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (secretKeyBase64 == null) {
            throw new IllegalStateException("JWT secret key is not configured");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");
    }

    // Updated to include userStatus parameter
    public String generateToken(String username, String role, Profile profile, String userStatus) {
        List<String> permissions = (profile != null && "ACTIVE".equals(profile.getStatus())) ?
                profile.getPermissions().stream()
                        .map(permission -> permission.getName())
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("profileCode", profile != null ? profile.getCode() : null)
                .claim("permissions", permissions)
                .claim("status", userStatus) // Add user status to the token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey)
                .compact();
    }
}