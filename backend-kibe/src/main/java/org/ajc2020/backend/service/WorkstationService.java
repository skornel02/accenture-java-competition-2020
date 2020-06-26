package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Worker;
import org.ajc2020.backend.model.Workstation;

import java.util.List;
import java.util.Optional;

public interface WorkstationService {

    void save(Workstation station);

    Optional<Workstation> findById(String id);

    List<Workstation> findAll();

    List<Workstation> findAllInUse();

    List<Workstation> findAllFree();

    List<Workstation> findAllOccupiable();

    void deleteById(String id);

    boolean occupiableWorkstationExists();

    Workstation occupyWorkstation(Worker worker);

    void freeWorkstations(Worker worker);

}
