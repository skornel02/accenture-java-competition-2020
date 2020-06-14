package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    void save(Admin admin);

    List<Admin> findAll();

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUuid(String uuid);

    void deleteByUuid(String uuid);
}
