package org.ajc2020.backend.service;

import org.ajc2020.backend.model.OfficeSettings;

public interface OfficeService {

    OfficeSettings getOfficeSetting();

    void updateOfficeSettings(OfficeSettings settings);

}
