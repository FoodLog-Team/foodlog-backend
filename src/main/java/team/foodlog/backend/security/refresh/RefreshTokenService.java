package team.foodlog.backend.security.refresh;

public interface RefreshTokenService<T, R> {

    R create(Long userId);

    void revoke(T token);

    R rotate(T token);

    void revokeAll(Long userId);

}
