package com.example.backend_pj4.infrastructure.databases.adapters;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.AdType;
import com.example.backend_pj4.domain.entities.Advertisement;
import com.example.backend_pj4.domain.repository.AdvertisementRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.AdvertisementMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaAdvertisementRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdvertisementRepositoryImpl implements AdvertisementRepository {

    private final JpaAdvertisementRepository jpaAdvertisementRepository;
    private final AdvertisementMapper advertisementMapper;

    @Override
    public Advertisement save(Advertisement advertisement) {
        var entity = advertisementMapper.toEntity(advertisement);
        var saved = jpaAdvertisementRepository.save(entity);
        return advertisementMapper.toDomain(saved);
    }

    @Override
    public Optional<Advertisement> findById(String adId) {
        return jpaAdvertisementRepository.findById(adId)
                .map(advertisementMapper::toDomain);
    }

    @Override
    public List<Advertisement> findAll() {
        return jpaAdvertisementRepository.findAll().stream()
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String adId) {
        jpaAdvertisementRepository.deleteById(adId);
    }

    @Override
    public List<Advertisement> findActiveAds() {
        return jpaAdvertisementRepository.findActiveAdsForCurrentDate(LocalDate.now()).stream()
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findActiveAdsByType(String adType) {
        return jpaAdvertisementRepository.findActiveByType(LocalDate.now(), AdType.valueOf(adType)).stream()
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findActiveAdsByPosition(String position) {
        return jpaAdvertisementRepository.findByPosition(position).stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsActive()))
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findCurrentAds(LocalDate currentDate) {
        return jpaAdvertisementRepository.findActiveAdsForCurrentDate(currentDate).stream()
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaAdvertisementRepository.findAll().stream()
                .filter(a -> a.getStartDate() != null 
                        && !a.getStartDate().isBefore(startDate) 
                        && !a.getStartDate().isAfter(endDate))
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByEndDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaAdvertisementRepository.findAll().stream()
                .filter(a -> a.getEndDate() != null 
                        && !a.getEndDate().isBefore(startDate) 
                        && !a.getEndDate().isAfter(endDate))
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findExpiredAds(LocalDate currentDate) {
        return jpaAdvertisementRepository.findAll().stream()
                .filter(a -> a.getEndDate() != null && a.getEndDate().isBefore(currentDate))
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByAdType(String adType) {
        return jpaAdvertisementRepository.findByAdType(AdType.valueOf(adType)).stream()
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByPosition(String position) {
        return jpaAdvertisementRepository.findByPosition(position).stream()
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByCreatedBy(String userId) {
        return jpaAdvertisementRepository.findAll().stream()
                .filter(a -> a.getCreatedBy() != null && a.getCreatedBy().getUserId().equals(userId))
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findTopClickedAds(int limit) {
        return jpaAdvertisementRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(
                        b.getClickCount() != null ? b.getClickCount() : 0,
                        a.getClickCount() != null ? a.getClickCount() : 0))
                .limit(limit)
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findTopViewedAds(int limit) {
        return jpaAdvertisementRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(
                        b.getViewCount() != null ? b.getViewCount() : 0,
                        a.getViewCount() != null ? a.getViewCount() : 0))
                .limit(limit)
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByClickCountGreaterThan(Integer minClicks) {
        return jpaAdvertisementRepository.findAll().stream()
                .filter(a -> a.getClickCount() != null && a.getClickCount() > minClicks)
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByViewCountGreaterThan(Integer minViews) {
        return jpaAdvertisementRepository.findAll().stream()
                .filter(a -> a.getViewCount() != null && a.getViewCount() > minViews)
                .map(advertisementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveAds() {
        return jpaAdvertisementRepository.countByIsActiveTrue();
    }

    @Override
    public long countByAdType(String adType) {
        return jpaAdvertisementRepository.countByAdType(AdType.valueOf(adType));
    }

    @Override
    public Double calculateClickThroughRate(String adId) {
        return jpaAdvertisementRepository.findById(adId)
                .map(ad -> {
                    if (ad.getViewCount() == null || ad.getViewCount() == 0) {
                        return 0.0;
                    }
                    return (double) ad.getClickCount() / ad.getViewCount() * 100;
                })
                .orElse(0.0);
    }
}
