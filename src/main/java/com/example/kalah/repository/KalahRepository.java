package com.example.kalah.repository;

import com.example.kalah.domain.KalahGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KalahRepository
        extends JpaRepository<KalahGameEntity, Long> {

}
