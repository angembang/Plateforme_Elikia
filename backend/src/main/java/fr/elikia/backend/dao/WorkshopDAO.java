package fr.elikia.backend.dao;

import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.dao.idao.IDAOWorkshop;
import fr.elikia.backend.repository.WorkshopRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkshopDAO implements IDAOWorkshop {
    private final WorkshopRepository workshopRepository;

    public WorkshopDAO(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    @Override
    public List<Workshop> findAll() {
        return workshopRepository.findAll();

    }

    @Override
    public Workshop findById(Long workshopId) {
        return workshopRepository.findById(workshopId).orElse(null);

    }

    @Override
    public boolean deleteById(Long workshopId) {
        Workshop workshop = workshopRepository.findById(workshopId).orElse(null);

        if(workshop != null) {
            workshopRepository.delete(workshop);
            return true;
        }
        return false;

    }

    @Override
    public Workshop create(Workshop workshop) {
        return workshopRepository.save(workshop);

    }

    @Override
    public Workshop update(Workshop workshop) {
        if(!workshopRepository.existsById(workshop.getWorkshopId())) {
            return null;
        }
        return workshopRepository.save(workshop);

    }


    // Retrieve workshops by their visibility ordered by the start date desc
    @Override
    public Page<Workshop> findAllByVisibilityOrderByStartDateDesc(
            Visibility visibility,
            Pageable pageable) {
        return workshopRepository.findAllByVisibilityOrderByStartDateDesc(
                visibility,
                pageable
        );
    }

    // Retrieve all workshop ordered by the start date
    @Override
    public Page<Workshop> findAllByOrderByStartDateDesc(
            Pageable pageable) {
        return workshopRepository.findAllByOrderByStartDateDesc(pageable);

    }

    // Retrieve 4 latest workshops
    @Override
    public List<Workshop> findAllByOrderByStartDateDesc() {
        return workshopRepository.findAllByOrderByStartDateDesc();
    }
}
