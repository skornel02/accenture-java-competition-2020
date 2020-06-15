package org.ajc2020.spring1.controller;

import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.OfficeSettings;
import org.ajc2020.spring1.service.OfficeService;
import org.ajc2020.utility.communication.OfficeResource;
import org.ajc2020.utility.exceptions.ForbiddenException;
import org.ajc2020.utility.resource.PermissionLevel;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.ResourceBundle;

@RestController
@RequestMapping("office-settings")
public class OfficeController {


    private final SessionManager sessionManager;
    private final OfficeService officeService;

    public OfficeController(SessionManager sessionManager, OfficeService officeService) {
        this.sessionManager = sessionManager;
        this.officeService = officeService;
    }

    @GetMapping
    public OfficeResource returnOfficeSettings() {
        return officeService.getOfficeSetting().toResource();
    }

    @PatchMapping
    public OfficeResource updateOfficeSettings(@RequestBody OfficeResource newValues, Locale locale) {
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.superadmin"));
        }
        OfficeSettings currentSettings = officeService.getOfficeSetting();
        currentSettings.fromResource(newValues);
        officeService.updateOfficeSettings(currentSettings);
        return currentSettings.toResource();
    }
}
