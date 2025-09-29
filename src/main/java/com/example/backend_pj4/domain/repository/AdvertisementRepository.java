package com.example.backend_pj4.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Advertisement;

public interface AdvertisementRepository {
    // Basic CRUD
    Advertisement save(Advertisement advertisement);

    Optional<Advertisement> findById(Integer adId);

    List<Advertisement> findAll();

    void deleteById(Integer adId);

    // Active ads
    List<Advertisement> findActiveAds();

    List<Advertisement> findActiveAdsByType(String adType);

    List<Advertisement> findActiveAdsByPosition(String position);

    // Date-based queries
    List<Advertisement> findCurrentAds(LocalDate currentDate);

    List<Advertisement> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Advertisement> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    List<Advertisement> findExpiredAds(LocalDate currentDate);

    // Type and position
    List<Advertisement> findByAdType(String adType);

    List<Advertisement> findByPosition(String position);

    // Creator
    List<Advertisement> findByCreatedBy(Integer userId);

    // Performance queries
    List<Advertisement> findTopClickedAds(int limit);

    List<Advertisement> findTopViewedAds(int limit);

    List<Advertisement> findByClickCountGreaterThan(Integer minClicks);

    List<Advertisement> findByViewCountGreaterThan(Integer minViews);

    // Statistics
    long countActiveAds();

    long countByAdType(String adType);

    Double calculateClickThroughRate(Integer adId);
}
