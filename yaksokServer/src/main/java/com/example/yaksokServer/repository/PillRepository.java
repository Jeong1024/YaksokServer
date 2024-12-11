package com.example.yaksokServer.repository;

import com.example.yaksokServer.model.Pill;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PillRepository
{
    private final EntityManager em;

    public void insert(Pill pill) {
        em.persist(pill);
    }

    public Pill search(String itemSeq) {
        return em.find(Pill.class, itemSeq);
    }

    public void update(Pill pill) {
        em.persist(pill);
    }

    public void delete(String itemSeq) {
        Pill result = em.find(Pill.class, itemSeq);
        em.remove(result);
    }
}
