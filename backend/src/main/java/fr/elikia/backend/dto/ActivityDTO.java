package fr.elikia.backend.dto;

import fr.elikia.backend.bo.enums.Visibility;

import java.time.LocalDateTime;

public interface ActivityDTO {
    String getTitle();
    String getDescription();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    String getLocation();
    String getAddress();
    int getCapacity();
    Visibility getVisibility();
}
