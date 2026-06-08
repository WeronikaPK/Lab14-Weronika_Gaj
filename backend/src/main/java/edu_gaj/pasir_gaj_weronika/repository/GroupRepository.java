package edu_gaj.pasir_gaj_weronika.repository;

import edu_gaj.pasir_gaj_weronika.model.Group;
import edu_gaj.pasir_gaj_weronika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByOwner(User owner);
}