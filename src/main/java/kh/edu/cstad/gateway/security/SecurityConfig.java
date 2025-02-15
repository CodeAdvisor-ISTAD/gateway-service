package kh.edu.cstad.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.server.WebFilter;

import java.net.URI;
import java.util.stream.Stream;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RoleBasedAuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler;


    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                  @Value("${client-security-matchers}") String[] securityMatchers,
                                                  @Value("${client-permit-matchers}") String[] permitMatchers,
                                                  ReactiveClientRegistrationRepository repository) {

        // Apply this filter-chain only to resources needing sessions
        final var clientRoutes = Stream.of(securityMatchers).map(PathPatternParserServerWebExchangeMatcher::new)
                .map(ServerWebExchangeMatcher.class::cast)
                .toList();
        http.securityMatcher(new OrServerWebExchangeMatcher(clientRoutes));

        http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(permitMatchers).permitAll()
                        .anyExchange().authenticated()
                );

        http
                .oauth2Login(oAuth2LoginSpec -> {
                    oAuth2LoginSpec.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/"));
                    oAuth2LoginSpec.authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/401"));
                    oAuth2LoginSpec.authorizationRequestResolver(pkceResolver(repository));
//                    oAuth2LoginSpec.authenticationSuccessHandler(roleBasedAuthenticationSuccessHandler);

                })
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .oauth2Client(Customizer.withDefaults())
                .logout(logoutSpec -> logoutSpec
                        .logoutSuccessHandler(serverLogoutSuccessHandler()));

        return http.build();
    }

    private ServerOAuth2AuthorizationRequestResolver pkceResolver(ReactiveClientRegistrationRepository repository) {
        var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(repository);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    private ServerLogoutSuccessHandler serverLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        final String DEFAULT_LOGOUT_SUCCESS_URL = "/";
        URI logoutSuccessUrl = URI.create(DEFAULT_LOGOUT_SUCCESS_URL);
        redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(logoutSuccessUrl);

        return redirectServerLogoutSuccessHandler;
    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }

//    @Bean
//    public WebFilter logTokenRelay() {
//        return (exchange, chain) -> {
//            exchange.getRequest().getHeaders().forEach((name, values) -> {
//                System.out.println(name + ": " + values);
//            });
//            return chain.filter(exchange);
//        };
//    }
}
