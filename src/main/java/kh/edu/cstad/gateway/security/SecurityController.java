package kh.edu.cstad.gateway.security;

import kh.edu.cstad.gateway.dto.UserProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SecurityController {

    @GetMapping("/profile")
    UserProfile secured(@AuthenticationPrincipal Authentication auth) {
        OAuth2AuthenticationToken oauth2 = (OAuth2AuthenticationToken) auth;
        OidcUser oidcUser = (OidcUser) oauth2.getPrincipal();

        System.out.println("SHOW: " + oidcUser.getAttributes());

        String uuid = oidcUser.getAttribute("uuid");
        String reksmey1 = oidcUser.getIdToken().getClaimAsString("seu");

        return new UserProfile(
                oidcUser.getName(),
                uuid,
                reksmey1
        );
    }

}
