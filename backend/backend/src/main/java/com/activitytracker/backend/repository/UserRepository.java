package com.activitytracker.backend.repository;

import com.activitytracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByFriendCode(String friendCode);

    boolean existsByFriendCode(String friendCode);
}