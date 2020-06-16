package org.ajc2020.spring1.repository;

import org.ajc2020.spring1.model.OfficeSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficeSettingsRepository extends JpaRepository<OfficeSettings, String> {

    Optional<OfficeSettings> findBy();

}
