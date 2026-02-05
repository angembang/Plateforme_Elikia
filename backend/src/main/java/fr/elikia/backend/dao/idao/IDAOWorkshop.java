package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Workshop;

import java.util.List;

public interface IDAOWorkshop {
    List<Workshop> findAll();

    Workshop findById(Long workshopId);

    boolean deleteById(Long workshopId);

    Workshop create(Workshop workshop);

    Workshop update(Workshop workshop);
}
