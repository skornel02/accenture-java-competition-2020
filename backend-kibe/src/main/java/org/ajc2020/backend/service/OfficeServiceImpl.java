package org.ajc2020.backend.service;

import org.ajc2020.backend.model.OfficeSettings;
import org.ajc2020.backend.repository.OfficeSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfficeServiceImpl implements OfficeService {

    private final OfficeSettingsRepository settingRepository;

    @Autowired
    public OfficeServiceImpl(OfficeSettingsRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public synchronized OfficeSettings getOfficeSetting() {
        return settingRepository.findBy().orElseGet(() -> {
            OfficeSettings settings = new OfficeSettings();
            settings.setCapacity(250);
            settings.setOperationPercentage(0.2);
            settings.setCentimetersBetweenEmployeeStations(500);
            settingRepository.save(settings);
            return settings;
        });
    }

    @Override
    public void updateOfficeSettings(OfficeSettings settings) {
        if (settings.getCapacity() < 1)
            throw new IllegalArgumentException("Capacity must be a non-zero positive integer.");
        if (settings.getOperationPercentage() < 0 || settings.getOperationPercentage() > 1)
            throw new IllegalArgumentException("Operation percentage must be or between 0-1");
        if (settings.getCentimetersBetweenEmployeeStations() < 0)
            throw new IllegalArgumentException("Centimeters between employee stations must be a non-zero positive integer.");
        settingRepository.save(settings);
    }
}
