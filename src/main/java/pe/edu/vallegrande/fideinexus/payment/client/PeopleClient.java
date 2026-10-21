package pe.edu.vallegrande.fideinexus.payment.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.dto.PeopleInfo;

@Slf4j
@Component
public class PeopleClient {

    private final WebClient webClient;

    public PeopleClient(WebClient.Builder builder, @Value("${external.services.people.url:http://localhost:8765/api/v1/users}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public Mono<PeopleInfo> findById(Long id) {
        return extractToken()
                .flatMap(token -> webClient.get()
                        .uri("/{id}", id)
                        .headers(h -> {
                            if (token != null && !token.isBlank()) {
                                h.setBearerAuth(token);
                            }
                        })
                        .retrieve()
                        .bodyToMono(PeopleInfo.class))
                .switchIfEmpty(Mono.defer(() -> webClient.get()
                        .uri("/{id}", id)
                        .retrieve()
                        .bodyToMono(PeopleInfo.class)))
                .doOnError(err -> log.warn("[PeopleClient] Error al consultar persona por id={}: {}", id, err.getMessage()));
    }

    /**
     * Consulta la lista de feligreses dinámicamente según el tenantId especificado
     * o extraído del contexto de seguridad JWT (claims tenantId u organizationId).
     *
     * Endpoint invocado: http://localhost:8765/api/v1/users/feligreses?tenantId={tenantId}
     */
    public Flux<PeopleInfo> findFeligreses(Long tenantId) {
        return resolveContextAndCall(tenantId, null);
    }

    public Flux<PeopleInfo> findFeligreses(Long tenantId, String token) {
        return resolveContextAndCall(tenantId, token);
    }

    private Flux<PeopleInfo> resolveContextAndCall(Long explicitTenantId, String explicitToken) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .defaultIfEmpty(new DummyAuthentication())
                .flatMapMany(auth -> {
                    String token = explicitToken;
                    Long effectiveTenantId = explicitTenantId;

                    if (auth instanceof JwtAuthenticationToken jwtAuth) {
                        if (token == null || token.isBlank()) {
                            token = jwtAuth.getToken().getTokenValue();
                        }
                        if (effectiveTenantId == null) {
                            effectiveTenantId = extractTenantIdFromToken(jwtAuth);
                        }
                    }

                    return callFeligresesEndpoint(effectiveTenantId, token);
                })
                .switchIfEmpty(callFeligresesEndpoint(explicitTenantId, explicitToken))
                .doOnError(err -> log.error("[PeopleClient] Error al consultar feligreses (tenantId={}): {}", explicitTenantId, err.getMessage()));
    }

    private Flux<PeopleInfo> callFeligresesEndpoint(Long tenantId, String token) {
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/feligreses");
                    if (tenantId != null) {
                        builder.queryParam("tenantId", tenantId);
                    }
                    return builder.build();
                })
                .headers(headers -> {
                    if (token != null && !token.isBlank()) {
                        headers.setBearerAuth(token);
                    }
                })
                .retrieve()
                .bodyToFlux(PeopleInfo.class);
    }

    private Mono<String> extractToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(jwtAuth -> jwtAuth.getToken().getTokenValue());
    }

    private Long extractTenantIdFromToken(JwtAuthenticationToken jwtAuth) {
        Object tenantObj = jwtAuth.getTokenAttributes().get("tenantId");
        if (tenantObj == null) {
            tenantObj = jwtAuth.getTokenAttributes().get("organizationId");
        }
        if (tenantObj != null) {
            try {
                return Long.valueOf(tenantObj.toString());
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static class DummyAuthentication implements org.springframework.security.core.Authentication {
        @Override public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() { return java.util.List.of(); }
        @Override public Object getCredentials() { return null; }
        @Override public Object getDetails() { return null; }
        @Override public Object getPrincipal() { return "anonymous"; }
        @Override public boolean isAuthenticated() { return false; }
        @Override public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
        @Override public String getName() { return "anonymous"; }
    }
}
