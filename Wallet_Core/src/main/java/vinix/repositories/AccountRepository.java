package vinix.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vinix.entities.Account;


public interface AccountRepository extends JpaRepository<Account, Long>{
	Optional<Account> findByDocument(String document);
}
