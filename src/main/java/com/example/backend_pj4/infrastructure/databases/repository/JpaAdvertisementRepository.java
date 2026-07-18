package com.example.backend_pj4.infrastructure.databases.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.AdType;
import com.example.backend_pj4.infrastructure.databases.entities.AdvertisementEntity;

@Repository
public interface JpaAdvertisementRepository extends JpaRepository<AdvertisementEntity, String> {
    
    List<AdvertisementEntity> findByIsActiveTrue();
    
    @Query("SELECT a FROM AdvertisementEntity a WHERE a.isActive = true AND a.startDate <= :currentDate AND a.endDate >= :currentDate")
    List<AdvertisementEntity> findActiveAdsForCurrentDate(LocalDate currentDate);
    
    @Query("SELECT a FROM AdvertisementEntity a WHERE a.isActive = true AND a.startDate <= ?1 AND a.endDate >= ?1 AND a.adType = ?2")
    List<AdvertisementEntity> findActiveByType(LocalDate currentDate, AdType adType);
    
    List<AdvertisementEntity> findByAdType(AdType adType);
    
    List<AdvertisementEntity> findByPosition(String position);
    
    Page<AdvertisementEntity> findByIsActiveTrue(Pageable pageable);
    
    long countByIsActiveTrue();
    
    long countByAdType(AdType adType);
}
