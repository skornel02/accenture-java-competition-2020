package org.ajc2020.backend.repository;

import org.ajc2020.backend.model.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkstationRepository extends JpaRepository<Workstation, String> {
}
