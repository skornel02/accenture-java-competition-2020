package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Workstation;

import java.util.List;

public interface PlanRendererService {

    String createAdmin2DSVG(List<Workstation> workstations, List<Workstation> occupiable);

    String createAdmin3DSVG(List<Workstation> workstations, List<Workstation> occupiable);

    String createWorker2DSVG(List<Workstation> workstations, Workstation chosen);

}
