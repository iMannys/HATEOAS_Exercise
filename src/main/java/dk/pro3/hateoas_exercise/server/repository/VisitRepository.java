package dk.pro3.hateoas_exercise.server.repository;

import dk.pro3.hateoas_exercise.domain.Place;
import dk.pro3.hateoas_exercise.domain.Visit;

import java.util.List;
import java.util.Optional;

// Read-only: visits are part of the Trip aggregate. Add with trip.addVisit(...),
// remove with trip.removeVisit(...), and they are saved with their trip.
public interface VisitRepository
{
  List<Visit> findAll();
  Optional<Visit> findById(Long id);
  List<Visit> findByPlace(Place place);
  List<Visit> findByCountryCode(String countryCode);
  boolean existsById(Long id);
}
