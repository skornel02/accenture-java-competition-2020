package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.WorkStation;

import java.util.List;

public interface WorkStationService {

    void save(WorkStation station);

    List<WorkStation> findAll();

    List<WorkStation> findAllInUse();

    List<WorkStation> findAllFree();

    List<WorkStation> findAllOccupiable();

    void deleteById(String id);

}
