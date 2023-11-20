package lux.fm.bookingservice.repository.user;

import java.util.List;
import java.util.Optional;
import lux.fm.bookingservice.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u.telegramId
            FROM User u
            WHERE u.telegramId IS NOT NULL AND u.isDeleted = false""")
    List<Long> getAllByTelegramIdIsNotNull();
}
