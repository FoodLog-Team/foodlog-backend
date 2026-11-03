package team.foodlog.backend.security;

@FunctionalInterface
public interface AuthenticatedUserIdProvider<T> {

    T provide();

}
