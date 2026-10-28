package br.univates.fifa;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * API autenticada (extra): exige o header X-API-KEY nas rotas /fifa/**.
 * Se app.api-key estiver vazio, a autenticação fica desligada.
 * A documentação (Swagger) continua pública.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String apiKey;

    public ApiKeyFilter(@Value("${app.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return apiKey.isBlank() || !request.getRequestURI().startsWith("/fifa");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (apiKey.equals(req.getHeader("X-API-KEY"))) {
            chain.doFilter(req, res);
            return;
        }
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getOutputStream().write("{\"error\":\"X-API-KEY ausente ou inválida\"}".getBytes(StandardCharsets.UTF_8));
    }
}
