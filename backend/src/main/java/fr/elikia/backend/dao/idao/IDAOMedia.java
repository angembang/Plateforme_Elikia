package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.*;

import java.util.List;

public interface IDAOMedia {
    List<Media> findAll();

    Media findById(Long mediaId);

    List<Media> findByNews(News news);

    List<Media> findByEvent(Event event);

    List<Media> findByWorkshop(Workshop workshop);

    List<Media> findByAchievement(Achievement achievement);

    boolean deleteById(Long mediaId);

    Media create(Media media);

    Media update(Media media);
}
