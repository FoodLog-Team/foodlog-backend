package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.foodlog.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
