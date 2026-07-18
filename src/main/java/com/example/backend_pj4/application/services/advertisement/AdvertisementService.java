package com.example.backend_pj4.application.services.advertisement;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.AdType;
import com.example.backend_pj4.domain.entities.Advertisement;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.AdvertisementRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdvertisementService extends BaseService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;

    public Advertisement createAdvertisement(String title, String content, String imageUrl,
            String clickUrl, AdType adType, String position, LocalDate startDate, LocalDate endDate, String createdBy) {
        
        User creator = null;
        if (createdBy != null) {
            creator = userRepository.findById(createdBy)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Advertisement ad = Advertisement.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .clickUrl(clickUrl)
                .adType(adType)
                .position(position)
                .startDate(startDate)
                .endDate(endDate)
                .clickCount(0)
                .viewCount(0)
                .isActive(true)
                .build();

        ad.setCreatedBy(creator);

        return advertisementRepository.save(ad);
    }

    public Advertisement updateAdvertisement(String adId, String title, String content,
            String imageUrl, String clickUrl, AdType adType, String position,
            LocalDate startDate, LocalDate endDate, Boolean isActive) {
        
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found"));

        if (title != null) ad.setTitle(title);
        if (content != null) ad.setContent(content);
        if (imageUrl != null) ad.setImageUrl(imageUrl);
        if (clickUrl != null) ad.setClickUrl(clickUrl);
        if (adType != null) ad.setAdType(adType);
        if (position != null) ad.setPosition(position);
        if (startDate != null) ad.setStartDate(startDate);
        if (endDate != null) ad.setEndDate(endDate);
        if (isActive != null) ad.setIsActive(isActive);

        return advertisementRepository.save(ad);
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getActiveAds() {
        return advertisementRepository.findActiveAds();
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getActiveAdsByType(String adType) {
        return advertisementRepository.findActiveAdsByType(adType);
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getActiveAdsByPosition(String position) {
        return advertisementRepository.findActiveAdsByPosition(position);
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getCurrentAds() {
        return advertisementRepository.findCurrentAds(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getAllAds() {
        return advertisementRepository.findAll();
    }

    public Advertisement recordView(String adId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found"));
        ad.setViewCount(ad.getViewCount() != null ? ad.getViewCount() + 1 : 1);
        return advertisementRepository.save(ad);
    }

    public Advertisement recordClick(String adId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found"));
        ad.setClickCount(ad.getClickCount() != null ? ad.getClickCount() + 1 : 1);
        return advertisementRepository.save(ad);
    }

    public void deleteAdvertisement(String adId) {
        if (!advertisementRepository.findById(adId).isPresent()) {
            throw new ResourceNotFoundException("Advertisement not found");
        }
        advertisementRepository.deleteById(adId);
    }

    @Transactional(readOnly = true)
    public Double getClickThroughRate(String adId) {
        return advertisementRepository.calculateClickThroughRate(adId);
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getTopClickedAds(int limit) {
        return advertisementRepository.findTopClickedAds(limit);
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getTopViewedAds(int limit) {
        return advertisementRepository.findTopViewedAds(limit);
    }

    @Transactional(readOnly = true)
    public long countActiveAds() {
        return advertisementRepository.countActiveAds();
    }
}
