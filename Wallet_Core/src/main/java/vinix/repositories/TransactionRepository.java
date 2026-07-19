package vinix.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vinix.entities.Transaction;




public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	 List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}
