package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.OfficeSettings;

public interface OfficeService {

    OfficeSettings getOfficeSetting();

    void updateOfficeSettings(OfficeSettings settings);

}
