package team.foodlog.backend.security.jwt;

public interface JwtService {
    String generateAccessToken(Long userId);
}
