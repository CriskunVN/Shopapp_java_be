package com.springboottayjv.demolearntayjv.repository;

import com.springboottayjv.demolearntayjv.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
