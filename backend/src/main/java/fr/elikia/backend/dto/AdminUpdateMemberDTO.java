package fr.elikia.backend.dto;

import jakarta.validation.constraints.Pattern;

public class AdminUpdateMemberDTO {
    @Pattern(
            regexp = "PENDING|APPROVED|REJECTED|CANCELLED",
            message = "Invalid member status"
    )
    private String status;

    private String roleName;

    public AdminUpdateMemberDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
