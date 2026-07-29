package vinix.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vinix.entities.Role;


public interface RoleRepository extends JpaRepository<Role, Long>{
	Optional<Role> findByRoleName(String roleName);
}
