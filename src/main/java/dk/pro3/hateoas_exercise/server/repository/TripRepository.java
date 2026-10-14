package dk.pro3.hateoas_exercise.server.repository;

import dk.pro3.hateoas_exercise.domain.Trip;

import java.util.List;
import java.util.Optional;

public interface TripRepository
{
  List<Trip> findAll();
  Optional<Trip> findById(Long id);
  List<Trip> findByName(String name);
  Optional<Trip> findByVisitId(Long visitId);
  boolean existsById(Long id);
  Trip save(Trip trip);
  void deleteById(Long id);
}
