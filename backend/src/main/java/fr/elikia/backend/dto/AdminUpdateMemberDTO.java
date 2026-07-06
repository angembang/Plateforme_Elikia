package fr.elikia.backend.dto;

import fr.elikia.backend.bo.enums.RegistrationStatus;

public class AdminUpdateMemberDTO {
    private RegistrationStatus status;

    private String roleName;

    public AdminUpdateMemberDTO() {
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
