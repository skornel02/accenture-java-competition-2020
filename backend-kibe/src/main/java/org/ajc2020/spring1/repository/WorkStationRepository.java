package org.ajc2020.spring1.repository;

import org.ajc2020.spring1.model.WorkStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkStationRepository extends JpaRepository<WorkStation, String> {
}
