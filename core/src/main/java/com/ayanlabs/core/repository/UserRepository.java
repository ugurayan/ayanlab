package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByEmailAndAppId(String email, UUID appId);
    Optional<User> findByMobileAndAppId(String mobile, UUID appId);

    @Query("SELECT u FROM User u WHERE (u.email = :email OR u.mobile = :mobile) AND u.appId = :appId")
    Optional<User> findByEmailOrMobileAndAppId(@Param("email") String email, @Param("mobile") String mobile, @Param("appId") UUID appId);

    @Query("SELECT u FROM User u WHERE (u.email = :email OR u.mobile = :mobile) AND u.appId = :appId and u.id != :id")
    Optional<User> findByEmailOrMobileAndAppIdNotId(@Param("email") String email, @Param("mobile") String mobile, @Param("appId") UUID appId, @Param("id") UUID id);

    List<User> findAllByAppId(UUID appId, Pageable pageable);

    boolean existsByEmailAndAppId(String email, UUID appId);

    boolean existsByMobileAndAppId(String mobile, UUID appId);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE (u.email = :email OR u.mobile = :mobile) AND u.appId = :appId")
    boolean existsByEmailAndMobileAndAppId(@Param("email") String email, @Param("mobile") String mobile, @Param("appId") UUID appId);
}
