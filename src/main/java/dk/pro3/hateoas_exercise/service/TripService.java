package dk.pro3.hateoas_exercise.service;

import dk.pro3.hateoas_exercise.domain.Place;
import dk.pro3.hateoas_exercise.domain.Trip;
import dk.pro3.hateoas_exercise.domain.Visit;
import dk.pro3.hateoas_exercise.exception.CityNotFoundException;
import dk.pro3.hateoas_exercise.exception.CountryNotFoundException;
import dk.pro3.hateoas_exercise.exception.TripNotFoundException;
import dk.pro3.hateoas_exercise.exception.VisitNotFoundException;
import dk.pro3.hateoas_exercise.repository.CityRepository;
import dk.pro3.hateoas_exercise.repository.CountryRepository;
import dk.pro3.hateoas_exercise.repository.TripRepository;
import dk.pro3.hateoas_exercise.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Trip is the aggregate root for visits, so creating and removing visits happens here.
@Service
public class TripService
{
  private final TripRepository tripRepository;
  private final VisitRepository visitRepository;
  private final CountryRepository countryRepository;
  private final CityRepository cityRepository;

  public TripService(TripRepository tripRepository, VisitRepository visitRepository,
      CountryRepository countryRepository, CityRepository cityRepository)
  {
    this.tripRepository = tripRepository;
    this.visitRepository = visitRepository;
    this.countryRepository = countryRepository;
    this.cityRepository = cityRepository;
  }

  public List<Trip> getAllTrips()
  {
    return tripRepository.findAll();
  }

  public Trip getTrip(Long id)
  {
    return tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
  }

  public List<Trip> getTripsByName(String name)
  {
    return tripRepository.findByName(name);
  }

  // Visit has no reference to its trip (the trip owns its visits), so the trip
  // is found by searching the aggregate roots.
  public Trip getTripOfVisit(Long visitId)
  {
    return tripRepository.findByVisitId(visitId)
        .orElseThrow(() -> new VisitNotFoundException(visitId));
  }

  // The first visit is required: a trip has 1..* visits.
  // Give either countryCode or cityId for where the visit takes place.
  public Trip createTrip(String name, String countryCode, Long cityId,
      LocalDate arrivalDate, LocalDate departureDate, String journal)
  {
    Visit firstVisit = new Visit(findPlace(countryCode, cityId), arrivalDate, departureDate, journal);
    return tripRepository.save(new Trip(name, firstVisit));
  }

  public Trip renameTrip(Long id, String name)
  {
    Trip trip = getTrip(id);
    trip.setName(name);
    return trip;
  }

  // Give either countryCode or cityId for where the visit takes place.
  public Visit addVisit(Long tripId, String countryCode, Long cityId,
      LocalDate arrivalDate, LocalDate departureDate, String journal)
  {
    Trip trip = getTrip(tripId);
    Visit visit = new Visit(findPlace(countryCode, cityId), arrivalDate, departureDate, journal);
    trip.addVisit(visit);
    return visit;
  }

  public void removeVisit(Long tripId, Long visitId)
  {
    Trip trip = getTrip(tripId);
    Visit visit = visitRepository.findById(visitId)
        .orElseThrow(() -> new VisitNotFoundException(visitId));
    trip.removeVisit(visit);   // throws if the visit is in another trip or is the last one
  }

  public void deleteTrip(Long id)
  {
    getTrip(id);   // throws TripNotFoundException if missing
    tripRepository.deleteById(id);   // its visits go with it (same aggregate)
  }

  private Place findPlace(String countryCode, Long cityId)
  {
    if ((countryCode == null) == (cityId == null))
    {
      throw new IllegalArgumentException("A visit needs either a country code or a city id, not both");
    }
    if (cityId != null)
    {
      return cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
    }
    return countryRepository.findByCode(countryCode)
        .orElseThrow(() -> new CountryNotFoundException(countryCode));
  }
}
