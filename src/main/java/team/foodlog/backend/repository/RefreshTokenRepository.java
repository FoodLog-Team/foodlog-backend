package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.foodlog.backend.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
