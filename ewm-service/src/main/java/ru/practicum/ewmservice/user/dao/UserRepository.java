package ru.practicum.ewmservice.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.user.model.User;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
}
