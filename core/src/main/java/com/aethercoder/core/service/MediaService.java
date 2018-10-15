package com.aethercoder.core.service;


import com.aethercoder.core.entity.media.Media;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MediaService {

    Media updateMedia(Media media);

   Media createMedia(Media media);

    Page<Media> findMediasByPage(Integer page, Integer size, String name, Integer status, String languageType);

    List<Media> findMediaByLanguageAndStatusIsDefaultUsing(String languageType);

    Media modifyStatus(Media media);

    void sortMediaBySequence(Long[] tagsIdList);
}


