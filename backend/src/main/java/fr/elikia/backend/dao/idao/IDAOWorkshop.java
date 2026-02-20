package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.bo.Workshop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDAOWorkshop {
    List<Workshop> findAll();

    Workshop findById(Long workshopId);

    boolean deleteById(Long workshopId);

    Workshop create(Workshop workshop);

    Workshop update(Workshop workshop);

    // Retrieve workshops by their visibility ordered by the start date desc
    Page<Workshop> findAllByVisibilityOrderByStartDateDesc(
            Visibility visibility,
            Pageable pageable
    );

    // Retrieve all workshop ordered by the start date
    Page<Workshop> findAllByOrderByStartDateDesc(
            Pageable pageable
    );

    // Retrieve 4 latest workshops
    List<Workshop> findAllByOrderByStartDateDesc();

}
