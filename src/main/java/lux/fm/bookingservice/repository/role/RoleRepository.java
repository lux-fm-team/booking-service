package lux.fm.bookingservice.repository.role;

import lux.fm.bookingservice.model.Role;
import lux.fm.bookingservice.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(RoleName name);
}
