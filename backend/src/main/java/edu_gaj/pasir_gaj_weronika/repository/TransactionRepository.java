package edu_gaj.pasir_gaj_weronika.repository;

import edu_gaj.pasir_gaj_weronika.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu_gaj.pasir_gaj_weronika.model.User;
import java.util.List;
import java.time.LocalDateTime;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);
    List<Transaction> findByUser(User user);
    List<Transaction> findAllByUserAndTimestampGreaterThanEqual(
            User user,
            LocalDateTime timestamp
    );
}