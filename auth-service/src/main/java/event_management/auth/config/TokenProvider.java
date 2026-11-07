package event_management.auth.config;

import event_management.auth.model.Users;
import event_management.common.security.JwtSupport;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtSupport jwtSupport;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    public String gerarToken(Users user) {
        log.info("Gerando token para usuário: {}", user.getEmail());
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("nome", user.getNome())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(jwtSupport.getSigningKey())
                .compact();

        log.debug("Token gerado para usuário: {}", user.getEmail());
        return token;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}
