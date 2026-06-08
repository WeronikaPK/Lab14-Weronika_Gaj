package edu_gaj.pasir_gaj_weronika.service;

import edu_gaj.pasir_gaj_weronika.dto.GroupTransactionDTO;
import edu_gaj.pasir_gaj_weronika.model.*;
import edu_gaj.pasir_gaj_weronika.repository.DebtRepository;
import edu_gaj.pasir_gaj_weronika.repository.GroupRepository;
import edu_gaj.pasir_gaj_weronika.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupTransactionService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final CurrentUserService currentUserService;
    private final GroupNotificationService groupNotificationService;

    public GroupTransactionService(GroupRepository groupRepository,
                                   MembershipRepository membershipRepository,
                                   DebtRepository debtRepository,
                                   CurrentUserService currentUserService,
                                   GroupNotificationService groupNotificationService) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.debtRepository = debtRepository;
        this.currentUserService = currentUserService;
        this.groupNotificationService = groupNotificationService;
    }

    public boolean addGroupTransaction(GroupTransactionDTO dto) {
        User currentUser = currentUserService.getCurrentUser();

        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        List<Membership> members = membershipRepository.findByGroup(group);
        List<Membership> selectedMembers = selectParticipants(dto, members, currentUser);

        if (selectedMembers.isEmpty()) {
            throw new RuntimeException("Grupa nie ma czlonkow");
        }

        double amountPerUser = dto.getAmount() / selectedMembers.size();
        boolean expense = "EXPENSE".equals(dto.getType());

        for (Membership member : selectedMembers) {
            User otherUser = member.getUser();

            if (!otherUser.getId().equals(currentUser.getId())) {
                Debt debt = new Debt();
                debt.setGroup(group);
                debt.setAmount(amountPerUser);
                debt.setTitle(dto.getTitle());

                if (expense) {
                    debt.setDebtor(otherUser);
                    debt.setCreditor(currentUser);
                } else {
                    debt.setDebtor(currentUser);
                    debt.setCreditor(otherUser);
                }

                debtRepository.save(debt);
            }
        }

        groupNotificationService.notifyGroupExpenseAdded(
                group,
                dto.getTitle(),
                dto.getAmount(),
                amountPerUser,
                currentUser,
                selectedMembers
        );

        return true;
    }

    private List<Membership> selectParticipants(GroupTransactionDTO dto,
                                                List<Membership> members,
                                                User currentUser) {
        List<Long> selectedUserIds = dto.getSelectedUserIds();

        if (selectedUserIds == null || selectedUserIds.isEmpty()) {
            return members;
        }

        Set<Long> uniqueSelectedUserIds = new HashSet<>(selectedUserIds);

        List<Membership> selectedMembers = members.stream()
                .filter(membership -> uniqueSelectedUserIds.contains(membership.getUser().getId()))
                .toList();

        if (selectedMembers.size() != uniqueSelectedUserIds.size()) {
            throw new RuntimeException("Wszyscy wybrani uzytkownicy musza byc czlonkami grupy");
        }

        boolean currentUserSelected = selectedMembers.stream()
                .anyMatch(membership -> membership.getUser().getId().equals(currentUser.getId()));

        if (!currentUserSelected) {
            throw new RuntimeException("Aktualny uzytkownik musi byc uczestnikiem transakcji grupowej");
        }

        if (selectedMembers.size() < 2) {
            throw new RuntimeException("Transakcja grupowa wymaga co najmniej dwoch uczestnikow");
        }

        return selectedMembers;
    }
}