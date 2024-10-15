package io.sabitovka.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<I, M> {
    boolean existsById(I id);
    M create(M obj);
    Optional<M> findById(I id);
    List<M> findAll();
    boolean update(M obj);
    boolean deleteById(I id);
}
