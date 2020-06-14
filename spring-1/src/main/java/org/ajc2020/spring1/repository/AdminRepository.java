package org.ajc2020.spring1.repository;

import org.ajc2020.spring1.model.Admin;
import org.ajc2020.spring1.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Optional<Admin> findAdminByEmail(String email);

    List<Admin> findAll();

    Optional<Admin> findAdminByUuid(String uuid);
}
