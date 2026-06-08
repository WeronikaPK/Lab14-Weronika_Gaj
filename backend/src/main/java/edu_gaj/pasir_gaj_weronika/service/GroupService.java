package edu_gaj.pasir_gaj_weronika.service;

import edu_gaj.pasir_gaj_weronika.dto.GroupDTO;
import edu_gaj.pasir_gaj_weronika.dto.GroupResponseDTO;
import edu_gaj.pasir_gaj_weronika.model.Group;
import edu_gaj.pasir_gaj_weronika.model.Membership;
import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.repository.GroupRepository;
import edu_gaj.pasir_gaj_weronika.repository.MembershipRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final CurrentUserService currentUserService;

    public GroupService(GroupRepository groupRepository,
                        MembershipRepository membershipRepository,
                        CurrentUserService currentUserService) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.currentUserService = currentUserService;
    }

    public GroupResponseDTO createGroup(GroupDTO dto) {
        User currentUser = currentUserService.getCurrentUser();

        Group group = new Group();
        group.setName(dto.getName());
        group.setOwner(currentUser);

        Group savedGroup = groupRepository.save(group);

        Membership membership = new Membership();
        membership.setGroup(savedGroup);
        membership.setUser(currentUser);
        membershipRepository.save(membership);

        return mapToDTO(savedGroup);
    }

    public List<GroupResponseDTO> getMyGroups() {
        User currentUser = currentUserService.getCurrentUser();

        return membershipRepository.findByUser(currentUser)
                .stream()
                .map(Membership::getGroup)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));
    }

    @Transactional
    public void deleteGroup(Long id) {
        User currentUser = currentUserService.getCurrentUser();

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Tylko wlasciciel grupy moze usunac grupe");
        }

        membershipRepository.deleteByGroupId(id);
        groupRepository.delete(group);
    }

    private GroupResponseDTO mapToDTO(Group group) {
        return new GroupResponseDTO(
                group.getId(),
                group.getName(),
                group.getOwner().getId()
        );
    }
}