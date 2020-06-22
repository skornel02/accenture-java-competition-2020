package org.ajc2020.spring1.repository;

import org.ajc2020.spring1.model.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkstationRepository extends JpaRepository<Workstation, String> {
}
