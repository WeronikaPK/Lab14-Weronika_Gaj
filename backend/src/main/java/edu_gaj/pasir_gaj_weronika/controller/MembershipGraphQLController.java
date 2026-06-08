package edu_gaj.pasir_gaj_weronika.controller;

import edu_gaj.pasir_gaj_weronika.dto.MembershipDTO;
import edu_gaj.pasir_gaj_weronika.dto.MembershipResponseDTO;
import edu_gaj.pasir_gaj_weronika.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MembershipGraphQLController {

    private final MembershipService membershipService;

    public MembershipGraphQLController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @QueryMapping
    public List<MembershipResponseDTO> groupMembers(@Argument Long groupId) {
        return membershipService.getGroupMembers(groupId);
    }

    @MutationMapping
    public MembershipResponseDTO addMember(@Valid @Argument MembershipDTO membershipDTO) {
        return membershipService.addMember(membershipDTO);
    }

    @MutationMapping
    public Boolean removeMember(@Argument Long membershipId) {
        membershipService.removeMember(membershipId);
        return true;
    }
}