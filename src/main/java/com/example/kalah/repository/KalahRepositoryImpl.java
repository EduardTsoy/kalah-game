package com.example.kalah.repository;

import com.example.kalah.domain.KalahGameEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class KalahRepositoryImpl extends SimpleJpaRepository<KalahGameEntity, Long> implements KalahRepository {

    @Autowired
    public KalahRepositoryImpl(final EntityManager em) {
        super(KalahGameEntity.class, em);
    }

    @Override
    public <S extends KalahGameEntity> S saveAndFlush(final S entity) {
        return super.saveAndFlush(entity);
    }

}
