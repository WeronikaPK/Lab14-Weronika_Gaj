package edu_gaj.pasir_gaj_weronika.repository;

import edu_gaj.pasir_gaj_weronika.model.Group;
import edu_gaj.pasir_gaj_weronika.model.Membership;
import edu_gaj.pasir_gaj_weronika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserAndGroup(User user, Group group);

    List<Membership> findByGroup(Group group);

    List<Membership> findByUser(User user);

    void deleteByGroupId(Long groupId);
}