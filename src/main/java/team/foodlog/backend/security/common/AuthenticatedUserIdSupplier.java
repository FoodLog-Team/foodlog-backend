package team.foodlog.backend.security.common;

import java.util.function.Supplier;

@FunctionalInterface
public interface AuthenticatedUserIdSupplier<T> extends Supplier<T> {

    T get();

}
