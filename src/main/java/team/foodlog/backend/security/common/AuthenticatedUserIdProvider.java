package team.foodlog.backend.security.common;

@FunctionalInterface
public interface AuthenticatedUserIdProvider<T> {

    T provide();

}
