package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}