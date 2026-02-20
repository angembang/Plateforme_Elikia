package fr.elikia.backend.bo;

import fr.elikia.backend.bo.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityEntity {
    void setTitle(String title);
    void setDescription(String description);
    void setStartDate(LocalDateTime startDate);
    void setEndDate(LocalDateTime endDate);
    void setLocation(String location);
    void setAddress(String address);
    void setCapacity(int capacity);
    void setVisibility(Visibility visibility);

    List<Media> getMediaList();
}
