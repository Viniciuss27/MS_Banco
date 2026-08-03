package vinix.config;

import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final SecretKey secretKey;

    @Override
    //É chamado em todas requisição do Gateway
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
        
        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange); // continua sem autenticar
        }

        String token = header.substring(7); // remove o "Bearer " (7 caracteres)

        try {
            // Valida a assinatura do token
            String email = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject(); 

            var authentication = new UsernamePasswordAuthenticationToken(email, null, List.of());

            // no servlet é automatico, no reativo é manual
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (JwtException e) {
            // Token inválido/expirado/forjado -> devolve 401 e encerra
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}