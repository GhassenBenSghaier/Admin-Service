package tn.esprit.adminservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    @Value("${jwt.secret}")
//    private String secretKeyBase64;
//
//    private javax.crypto.SecretKey secretKey;
//
//    private static final long EXPIRATION_MS = 86400000; // 24 hours
//
//    @PostConstruct
//    public void init() {
//        if (secretKeyBase64 == null) {
//            throw new IllegalStateException("JWT secret key is not configured. Please set 'jwt.secret' in application.yml");
//        }
//        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
//        this.secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
//    }
//
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//    }
//}

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
        this.secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateToken(String username, String role) { // Added role parameter
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}