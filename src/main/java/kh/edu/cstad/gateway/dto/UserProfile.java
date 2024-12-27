package kh.edu.cstad.gateway.dto;

public record UserProfile(
        String userUuid,
        String username,
        String fullName,
        String email,
        String profileImage
) {
}


