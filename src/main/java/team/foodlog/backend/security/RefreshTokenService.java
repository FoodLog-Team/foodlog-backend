package team.foodlog.backend.security;

public interface RefreshTokenService<T, R> {

    R create(Long userId);

    void revoke(T token);

    R rotate(T token);

    void revokeAll(Long userId);

}
