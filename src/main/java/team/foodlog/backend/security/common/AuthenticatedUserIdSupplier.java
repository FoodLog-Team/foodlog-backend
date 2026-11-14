package team.foodlog.backend.security.common;

import java.util.function.Supplier;

@FunctionalInterface
public interface AuthenticatedUserIdSupplier extends Supplier<Long> {

    Long get();

}
