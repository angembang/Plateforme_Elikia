package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Achievement;

import java.util.List;

public interface IDAOAchievement {
    List<Achievement> findAll();

    Achievement findById(Long achievementId);

    boolean deleteById(Long achievementId);

    Achievement create(Achievement achievement);

   Achievement update(Achievement achievement);
}
