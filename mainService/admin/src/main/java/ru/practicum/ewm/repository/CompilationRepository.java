package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
}