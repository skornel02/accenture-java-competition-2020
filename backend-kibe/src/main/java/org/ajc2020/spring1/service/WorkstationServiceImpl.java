package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.model.Workstation;
import org.ajc2020.spring1.repository.WorkstationRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkstationServiceImpl implements WorkstationService {

    private final WorkstationRepository repository;
    private final OfficeService officeService;

    private final Random random;

    public WorkstationServiceImpl(WorkstationRepository repository, OfficeService officeService) {
        this.repository = repository;
        this.officeService = officeService;

        this.random = new Random();
    }

    @Override
    public void save(Workstation station) {
        repository.save(station);
    }

    @Override
    public Optional<Workstation> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Workstation> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Workstation> findAllInUse() {
        return findAll().stream()
                .filter(station -> station.getOccupier() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Workstation> findAllFree() {
        return findAll().stream()
                .filter(station -> station.getOccupier() == null)
                .filter(Workstation::isEnabled)
                .collect(Collectors.toList());
    }

    @Override
    public List<Workstation> findAllOccupiable() {
        // naive implementation
        double minimumDistance = officeService.getOfficeSetting().getCentimetersBetweenEmployeeStations();
        List<Workstation> matchingStations = new LinkedList<>();
        List<Workstation> usedStations = findAllInUse();
        for (Workstation station : findAllFree()) {
            if (usedStations.stream().anyMatch(used
                    -> used.unitToCentimeter(used.distanceFrom(station)) < minimumDistance))
                continue;
            matchingStations.add(station);
        }
        return matchingStations;
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public Workstation occupyWorkstation(Worker worker) {
        synchronized (random) {
            List<Workstation> usableWorkstations = findAllOccupiable();
            Workstation chosen = usableWorkstations.get(random.nextInt(usableWorkstations.size()));
            chosen.setOccupier(worker);
            save(chosen);
            return chosen;
        }
    }

    @Override
    public void freeWorkstations(Worker worker) {
        synchronized (random) {
            List<Workstation> stations = findAllInUse().stream()
                    .filter(station -> Objects.equals(station.getOccupier(), worker))
                    .collect(Collectors.toList());
            stations.forEach(station -> {
                station.clearOccupier();
                save(station);
            });
        }
    }
}
