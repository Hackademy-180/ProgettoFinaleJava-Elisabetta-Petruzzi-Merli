package it.aulab.progetto_finale_java.services;

import it.aulab.progetto_finale_java.models.CareerRequest;
import it.aulab.progetto_finale_java.models.User;

// per togliere gli errori da operationController
public interface CareerRequestService {
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);
    void save(CareerRequest careerRequest, User user);
    void careerAccept(Long requestId);
    CareerRequest find(Long id);
}
