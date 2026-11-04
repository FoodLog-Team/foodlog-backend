package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.foodlog.backend.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
