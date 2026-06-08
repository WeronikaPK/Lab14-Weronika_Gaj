package edu_gaj.pasir_gaj_weronika.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupNotificationDTO {

    private String type;
    private Long groupId;
    private String groupName;
    private String title;
    private Double amount;
    private Double userShare;
    private String createdByEmail;
    private String message;
}