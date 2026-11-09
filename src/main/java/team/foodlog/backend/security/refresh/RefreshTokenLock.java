package team.foodlog.backend.security.refresh;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenLock {

    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.MANDATORY)
    public void lock(Long userId) {
        entityManager.createNativeQuery("""
                SELECT pg_advisory_xact_lock(?)
        """).setParameter(1, userId).getSingleResult();
    }

}
