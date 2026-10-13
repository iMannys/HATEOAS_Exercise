package dk.pro3.hateoas_exercise.service;

import dk.pro3.hateoas_exercise.domain.City;
import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.Trip;
import dk.pro3.hateoas_exercise.domain.Visit;
import dk.pro3.hateoas_exercise.domain.enums.Continent;
import dk.pro3.hateoas_exercise.exception.CityNotFoundException;
import dk.pro3.hateoas_exercise.exception.CountryAlreadyExistsException;
import dk.pro3.hateoas_exercise.exception.CountryNotFoundException;
import dk.pro3.hateoas_exercise.exception.TripNotFoundException;
import dk.pro3.hateoas_exercise.exception.VisitNotFoundException;
import dk.pro3.hateoas_exercise.repository.InMemoryCityRepository;
import dk.pro3.hateoas_exercise.repository.InMemoryCountryRepository;
import dk.pro3.hateoas_exercise.repository.InMemoryTripRepository;
import dk.pro3.hateoas_exercise.repository.InMemoryVisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// The services wired to the real in-memory repositories, without Spring: the rules
// that span several aggregates (lookups, "cannot delete while in use") live here.
class ServiceTest
{
  private static final LocalDate MAY_1 = LocalDate.of(2026, 5, 1);
  private static final LocalDate MAY_3 = LocalDate.of(2026, 5, 3);

  private CountryService countryService;
  private CityService cityService;
  private TripService tripService;
  private VisitService visitService;

  private Country denmark;
  private City aarhus;

  @BeforeEach void setUp()
  {
    InMemoryCountryRepository countryRepository = new InMemoryCountryRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository(countryRepository);
    InMemoryTripRepository tripRepository = new InMemoryTripRepository();
    InMemoryVisitRepository visitRepository = new InMemoryVisitRepository(tripRepository);

    countryService = new CountryService(countryRepository, cityRepository, visitRepository);
    cityService = new CityService(cityRepository, countryRepository, visitRepository);
    tripService = new TripService(tripRepository, visitRepository, countryRepository, cityRepository);
    visitService = new VisitService(visitRepository, countryRepository);

    denmark = countryService.createCountry("Denmark", "DK", Set.of(Continent.EUROPE),
        "Copenhagen", 660_000);
    aarhus = cityService.createCity("DK", "Aarhus", 300_000);
  }

  // --- countries and cities ---

  @Test void countryCodeMustBeUnique()
  {
    assertThatThrownBy(() -> countryService.createCountry("Denmark again", "DK",
        Set.of(Continent.EUROPE), "Copenhagen", 1))
        .isInstanceOf(CountryAlreadyExistsException.class);
  }

  @Test void citiesOfUnknownCountryIsNotFoundInsteadOfEmpty()
  {
    assertThatThrownBy(() -> cityService.getCitiesByCountry("SE"))
        .isInstanceOf(CountryNotFoundException.class);
  }

  @Test void createdCityIsFoundById()
  {
    assertThat(cityService.getCity(aarhus.getId())).isSameAs(aarhus);
    assertThat(cityService.getCitiesByCountry("DK")).contains(aarhus, denmark.getCapital());
  }

  @Test void cityWithVisitsCannotBeDeleted()
  {
    tripService.createTrip("Aarhus", null, aarhus.getId(), MAY_1, MAY_3, null);

    assertThatThrownBy(() -> cityService.deleteCity(aarhus.getId()))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test void deletedCityIsGone()
  {
    cityService.deleteCity(aarhus.getId());

    assertThatThrownBy(() -> cityService.getCity(aarhus.getId()))
        .isInstanceOf(CityNotFoundException.class);
  }

  @Test void countryWithVisitsCannotBeDeleted()
  {
    tripService.createTrip("Denmark", "DK", null, MAY_1, MAY_3, null);

    assertThatThrownBy(() -> countryService.deleteCountry("DK"))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test void capitalCanBeChanged()
  {
    countryService.changeCapital("DK", aarhus.getId());

    assertThat(countryService.getCountry("DK").getCapital()).isSameAs(aarhus);
  }

  // --- trips and visits ---

  @Test void visitNeedsExactlyOnePlace()
  {
    assertThatThrownBy(() -> tripService.createTrip("Both", "DK", aarhus.getId(), MAY_1, MAY_3, null))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> tripService.createTrip("Neither", null, null, MAY_1, MAY_3, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void visitToUnknownPlaceIsNotFound()
  {
    assertThatThrownBy(() -> tripService.createTrip("Trip", null, -1L, MAY_1, MAY_3, null))
        .isInstanceOf(CityNotFoundException.class);
    assertThatThrownBy(() -> tripService.createTrip("Trip", "SE", null, MAY_1, MAY_3, null))
        .isInstanceOf(CountryNotFoundException.class);
  }

  @Test void visitsAddedToATripAreFoundByVisitService()
  {
    Trip trip = tripService.createTrip("Denmark", "DK", null, MAY_1, MAY_3, null);
    Visit added = tripService.addVisit(trip.getId(), null, aarhus.getId(), MAY_3, MAY_3, "ARoS");

    assertThat(visitService.getVisit(added.getId())).isSameAs(added);
    assertThat(visitService.getVisitsByCountry("DK")).hasSize(2);
  }

  @Test void tripOfVisitIsFound()
  {
    Trip trip = tripService.createTrip("Denmark", "DK", null, MAY_1, MAY_3, null);
    Visit visit = trip.getVisits().getFirst();

    assertThat(tripService.getTripOfVisit(visit.getId())).isSameAs(trip);
  }

  @Test void tripOfUnknownVisitIsNotFound()
  {
    assertThatThrownBy(() -> tripService.getTripOfVisit(-1L))
        .isInstanceOf(VisitNotFoundException.class);
  }

  @Test void removedVisitIsGone()
  {
    Trip trip = tripService.createTrip("Denmark", "DK", null, MAY_1, MAY_3, null);
    Visit second = tripService.addVisit(trip.getId(), null, aarhus.getId(), MAY_3, MAY_3, null);

    tripService.removeVisit(trip.getId(), second.getId());

    assertThatThrownBy(() -> visitService.getVisit(second.getId()))
        .isInstanceOf(VisitNotFoundException.class);
  }

  @Test void deletingATripDeletesItsVisits()
  {
    Trip trip = tripService.createTrip("Denmark", "DK", null, MAY_1, MAY_3, null);
    Long visitId = trip.getVisits().getFirst().getId();

    tripService.deleteTrip(trip.getId());

    assertThatThrownBy(() -> tripService.getTrip(trip.getId()))
        .isInstanceOf(TripNotFoundException.class);
    assertThatThrownBy(() -> visitService.getVisit(visitId))
        .isInstanceOf(VisitNotFoundException.class);
  }
}
