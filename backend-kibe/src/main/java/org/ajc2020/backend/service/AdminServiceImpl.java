package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Admin;
import org.ajc2020.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public void save(Admin admin) {
        adminRepository.save(admin);
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findAdminByEmail(email);
    }

    @Override
    public Optional<Admin> findByUuid(String uuid) {
        return adminRepository.findAdminByUuid(uuid);
    }

    @Override
    public void deleteByUuid(String uuid) {
        adminRepository.deleteById(uuid);
    }
}
