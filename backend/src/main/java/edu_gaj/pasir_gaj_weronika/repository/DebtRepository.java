package edu_gaj.pasir_gaj_weronika.repository;

import edu_gaj.pasir_gaj_weronika.model.Debt;
import edu_gaj.pasir_gaj_weronika.model.Group;
import edu_gaj.pasir_gaj_weronika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    List<Debt> findByGroup(Group group);

    List<Debt> findByDebtor(User debtor);

    List<Debt> findByCreditor(User creditor);
}