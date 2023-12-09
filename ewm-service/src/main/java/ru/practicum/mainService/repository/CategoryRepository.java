package ru.practicum.mainService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainService.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}