package kh.edu.cstad.gateway.security;

import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.ParseException;
import java.util.List;

@Component
@Slf4j
public class RoleBasedAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    public RoleBasedAuthenticationSuccessHandler(ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            return authorizedClientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName())
                    .flatMap(authorizedClient -> {
                        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                        log.info("Access Token:: {}", accessToken.getTokenValue());
//                        if (accessToken != null) {
//                            return extractRolesFromToken(accessToken.getTokenValue())
//                                    .flatMap(roles -> {
//
//                                        String targetUrl = determineTargetUrl(roles);
//
////                                        webFilterExchange.getExchange().getResponse().setStatusCode(org.springframework.http.HttpStatus.FOUND);
////                                        webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create(targetUrl));
//
//                                        return webFilterExchange.getExchange().getResponse().setComplete();
//                                    });
//                        }
                        return Mono.empty();
                    });
        }
        return Mono.empty();
    }

    private Mono<List<String>> extractRolesFromToken(String token) {
        try {

            SignedJWT signedJWT = SignedJWT.parse(token);

            // Extract the 'roles' claim from the JWT
            List<String> roles = signedJWT.getJWTClaimsSet().getStringListClaim("roles");

            return Mono.just(roles != null ? roles : List.of());
        } catch (ParseException e) {
            return Mono.error(new RuntimeException("Failed to parse Access Token", e));
        }
    }

    private String determineTargetUrl(List<String> roles) {
        if (roles.contains("ADMIN")) {
            return "/organizer";
        } else if (roles.contains("ORGANIZER")) {
            return "/organizer";
        } else {
            return "/";
        }
    }
}
