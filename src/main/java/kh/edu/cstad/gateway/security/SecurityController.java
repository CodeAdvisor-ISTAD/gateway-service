package kh.edu.cstad.gateway.security;

import kh.edu.cstad.gateway.dto.UserProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping("/profile")
    UserProfile secured(Authentication auth) {

        Object oauth2 = auth.getPrincipal();
        if (oauth2 instanceof OAuth2User) {
            System.out.println("OAUTH2USER: " + oauth2);
        }

        OidcUser oidcUser = (OidcUser) oauth2;

        String userUuid = oidcUser.getIdToken().getClaimAsString("userUuid");
        String username = oidcUser.getIdToken().getClaimAsString("username");
        String fullName = oidcUser.getIdToken().getClaimAsString("fullName");
        String email = oidcUser.getIdToken().getClaimAsString("email");
        String profileImage = oidcUser.getIdToken().getClaimAsString("profileImage");
        return new UserProfile(
                userUuid,
                username,
                fullName,
                email,
                profileImage
        );

    }
}