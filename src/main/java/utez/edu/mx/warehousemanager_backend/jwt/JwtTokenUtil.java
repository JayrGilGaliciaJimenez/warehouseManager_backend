package utez.edu.mx.warehousemanager_backend.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenUtil {

    private static final long EXPIRE_DURATION = 24L * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secretKey;

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Token is null, empty, or contains errors {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT", ex);
        } catch (JwtException ex) {
            log.error("Signature validation failed");
        }
        return false;
    }

    public String generatedToken(UserModel userModel) {
        return Jwts.builder()
                .setSubject(String.format("%s, %s", userModel.getId(), userModel.getEmail()))
                .setIssuer("warehouse_manager")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
