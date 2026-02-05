package fr.elikia.backend.dao;

import fr.elikia.backend.bo.*;
import fr.elikia.backend.dao.idao.IDAOMedia;
import fr.elikia.backend.repository.MediaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaDAO implements IDAOMedia {
    private final MediaRepository mediaRepository;

    public MediaDAO(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    // Retrieve all media from database
    @Override
    public List<Media> findAll() {
        return mediaRepository.findAll();

    }

    // Retrieve one media by its unique identifier
    @Override
    public Media findById(Long mediaId) {
        return mediaRepository.findById(mediaId).orElse(null);

    }

    // Retrieve all media linked to a given news
    @Override
    public List<Media> findByNews(News news) {
        return mediaRepository.findAllByNews(news);
    }

    // Retrieve all media linked to a given event
    @Override
    public List<Media> findByEvent(Event event) {
        return mediaRepository.findAllByEvent(event);
    }

    // Retrieve all media linked to a given workshop
    @Override
    public List<Media> findByWorkshop(Workshop workshop) {
        return mediaRepository.findAllByWorkshop(workshop);
    }

    // Retrieve all media linked to a given achievement
    @Override
    public List<Media> findByAchievement(Achievement achievement) {
        return mediaRepository.findAllByAchievement(achievement);
    }

    @Override
    public boolean deleteById(Long mediaId) {
        Media media = mediaRepository.findById(mediaId).orElse(null);

        if(media != null) {
            mediaRepository.delete(media);
            return true;
        }
        return false;

    }

    @Override
    public Media create(Media media) {
        return mediaRepository.save(media);

    }

    @Override
    public Media update(Media media) {
        if(!mediaRepository.existsById(media.getMediaId())) {
            return null;
        }
        return mediaRepository.save(media);

    }
}
