package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.dao.idao.IDAOWorkshop;
import fr.elikia.backend.repository.WorkshopRepository;
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
}
