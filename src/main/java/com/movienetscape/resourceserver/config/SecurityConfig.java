package com.movienetscape.resourceserver.config;


import com.movienetscape.resourceserver.dto.AuthorisationDeniedResponse;
import com.movienetscape.resourceserver.exception.TokenRevokedException;

import com.movienetscape.resourceserver.service.TokenRevocationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
public class SecurityConfig {


    private final TokenRevocationService revocationService;

    public SecurityConfig(TokenRevocationService revocationService) {
        this.revocationService = revocationService;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/v1/users/**").hasRole("USER")
                        .pathMatchers("/api/v1/accounts/**").hasRole("USER")
                        .pathMatchers("api/v1/auth/refresh-token").hasRole("USER")
                        .pathMatchers("/api/v1/auth/logout").hasRole("USER")
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(revocationService));
                            jwt.jwkSetUri("http://localhost:8081/auth/key/jwks");
                        })
                );
        return http.build();
    }


    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter(TokenRevocationService revocationService) {
        return new Converter<Jwt, Mono<AbstractAuthenticationToken>>() {
            @Override
            public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
                String role = jwt.getClaim("role");
                String jti = jwt.getId();

                return Mono.just(jwt)
                        .flatMap(token -> {

                            if (jti != null && revocationService.isTokenRevoked("blacklist:" + jti)) {

                                return Mono.error(new TokenRevokedException("Access Denied: Token has been revoked."));
                            }

                            Collection<GrantedAuthority> authorities = new ArrayList<>();
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                            return Mono.just((AbstractAuthenticationToken) new JwtAuthenticationToken(token, authorities));
                        });
            }
        };
    }





}
