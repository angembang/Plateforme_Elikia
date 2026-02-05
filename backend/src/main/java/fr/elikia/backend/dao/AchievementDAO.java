package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Achievement;
import fr.elikia.backend.dao.idao.IDAOAchievement;
import fr.elikia.backend.repository.AchievementRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AchievementDAO implements IDAOAchievement {
    private final AchievementRepository achievementRepository;

    public AchievementDAO(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    public List<Achievement> findAll() {
        return achievementRepository.findAll();
    }

    @Override
    public Achievement findById(Long achievementId) {
        return achievementRepository.findById(achievementId).orElse(null);
    }

    @Override
    public boolean deleteById(Long achievementId) {
        Achievement achievement = achievementRepository.findById(achievementId).orElse(null);

        if(achievement != null) {
            achievementRepository.delete(achievement);
            return true;
        }
        return false;
    }

    @Override
    public Achievement create(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public Achievement update(Achievement achievement) {
        if(!achievementRepository.existsById(achievement.getAchievementId())) {
            return null;
        }
        return achievementRepository.save(achievement);
    }
}
