package edu_gaj.pasir_gaj_weronika.service;

import edu_gaj.pasir_gaj_weronika.dto.MembershipDTO;
import edu_gaj.pasir_gaj_weronika.dto.MembershipResponseDTO;
import edu_gaj.pasir_gaj_weronika.model.Group;
import edu_gaj.pasir_gaj_weronika.model.Membership;
import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.repository.GroupRepository;
import edu_gaj.pasir_gaj_weronika.repository.MembershipRepository;
import edu_gaj.pasir_gaj_weronika.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public MembershipService(MembershipRepository membershipRepository,
                             GroupRepository groupRepository,
                             UserRepository userRepository,
                             CurrentUserService currentUserService) {
        this.membershipRepository = membershipRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public MembershipResponseDTO addMember(MembershipDTO dto) {
        User currentUser = currentUserService.getCurrentUser();

        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Tylko wlasciciel grupy moze dodawac czlonkow");
        }

        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono uzytkownika"));

        if (membershipRepository.findByUserAndGroup(user, group).isPresent()) {
            throw new RuntimeException("Uzytkownik jest juz czlonkiem tej grupy");
        }

        Membership membership = new Membership();
        membership.setGroup(group);
        membership.setUser(user);

        Membership saved = membershipRepository.save(membership);

        return mapToDTO(saved);
    }

    public List<MembershipResponseDTO> getGroupMembers(Long groupId) {
        assertCurrentUserIsGroupMember(groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        return membershipRepository.findByGroup(group)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void removeMember(Long membershipId) {
        User currentUser = currentUserService.getCurrentUser();

        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono czlonkostwa"));

        Group group = membership.getGroup();

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Tylko wlasciciel grupy moze usuwac czlonkow");
        }

        if (membership.getUser().getId().equals(group.getOwner().getId())) {
            throw new RuntimeException("Nie mozna usunac wlasciciela grupy");
        }

        membershipRepository.delete(membership);
    }

    public void assertCurrentUserIsGroupMember(Long groupId) {
        User currentUser = currentUserService.getCurrentUser();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        if (membershipRepository.findByUserAndGroup(currentUser, group).isEmpty()) {
            throw new RuntimeException("Nie masz dostepu do tej grupy");
        }
    }

    public void assertUserIsGroupMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono uzytkownika"));

        if (membershipRepository.findByUserAndGroup(user, group).isEmpty()) {
            throw new RuntimeException("Uzytkownik nie jest czlonkiem tej grupy");
        }
    }

    private MembershipResponseDTO mapToDTO(Membership membership) {
        return new MembershipResponseDTO(
                membership.getId(),
                membership.getUser().getId(),
                membership.getGroup().getId(),
                membership.getUser().getEmail()
        );
    }
}