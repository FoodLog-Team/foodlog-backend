package team.foodlog.backend.security;

public interface JwtService {
    String generateAccessToken(Long userId, String email);
}
