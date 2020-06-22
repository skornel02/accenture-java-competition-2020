package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.WorkStation;
import org.ajc2020.spring1.repository.WorkStationRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkStationServiceImpl implements WorkStationService {

    private final WorkStationRepository repository;
    private final OfficeService officeService;

    public WorkStationServiceImpl(WorkStationRepository repository, OfficeService officeService) {
        this.repository = repository;
        this.officeService = officeService;
    }

    @Override
    public void save(WorkStation station) {
        repository.save(station);
    }

    @Override
    public List<WorkStation> findAll() {
        return repository.findAll();
    }

    @Override
    public List<WorkStation> findAllInUse() {
        return findAll().stream()
                .filter(station -> station.getOccupier() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkStation> findAllFree() {
        return findAll().stream()
                .filter(station -> station.getOccupier() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkStation> findAllOccupiable() {
        // naive implementation
        double minimumDistance = officeService.getOfficeSetting().getCentimetersBetweenEmployeeStations();
        List<WorkStation> matchingStations = new LinkedList<>();
        List<WorkStation> usedStations = findAllInUse();
        for (WorkStation station : findAll()) {
            if (station.getOccupier() != null || usedStations.stream()
                    .anyMatch(used -> used.pixelToCentimeter(used.distanceFrom(station)) < minimumDistance))
                continue;
            matchingStations.add(station);
        }
        return matchingStations;
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
