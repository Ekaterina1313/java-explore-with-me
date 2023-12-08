package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.common.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    @Query("SELECT c FROM Compilation c WHERE c.pinned = :pinned")
    Page<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
