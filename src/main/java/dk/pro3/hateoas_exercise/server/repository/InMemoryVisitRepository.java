package dk.pro3.hateoas_exercise.server.repository;

import dk.pro3.hateoas_exercise.domain.Place;
import dk.pro3.hateoas_exercise.domain.Visit;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository public class InMemoryVisitRepository implements VisitRepository
{
  // No map of its own: visits live inside their trips (the aggregate), so there is
  // only one copy and nothing to keep in sync. Every answer is built from the trips
  // in TripRepository (composition, not inheritance).
  private final TripRepository tripRepository;

  public InMemoryVisitRepository(TripRepository tripRepository)
  {
    this.tripRepository = tripRepository;
  }

  @Override public List<Visit> findAll()
  {
    return tripRepository.findAll().stream()
        .flatMap(trip -> trip.getVisits().stream()).sorted(Comparator.comparing(
            Visit::getId))   // ids only increase: creation order
        .toList();
  }

  @Override public Optional<Visit> findById(Long id)
  {
    return findAll().stream().filter(visit -> visit.getId().equals(id))
        .findFirst();
  }

  @Override public List<Visit> findByPlace(Place place)
  {
    return findAll().stream().filter(visit -> visit.getPlace() == place)
        .toList();
  }

  @Override public List<Visit> findByCountryCode(String countryCode)
  {
    return findAll().stream()
        .filter(visit -> visit.getCountry().getCode().equals(countryCode))
        .toList();
  }

  @Override public boolean existsById(Long id)
  {
    return findById(id).isPresent();
  }
}
