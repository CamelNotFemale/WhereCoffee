package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Query(value =
            "select * from users" +
            "    where username like concat('%',:username,'%') and id in (" +
            "           select user_id from user_roles" +
            "           join roles r on r.id = user_roles.role_id" +
            "           where name = :role" +
            "       )",
            nativeQuery = true)
    Page<User> findUserByUsernameAndRole(String username, String role, Pageable pageable);
}
