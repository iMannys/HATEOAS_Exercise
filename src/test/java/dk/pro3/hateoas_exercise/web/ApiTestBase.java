package dk.pro3.hateoas_exercise.web;

import dk.pro3.hateoas_exercise.domain.City;
import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.Trip;
import dk.pro3.hateoas_exercise.domain.Visit;
import dk.pro3.hateoas_exercise.domain.enums.Continent;
import dk.pro3.hateoas_exercise.service.CityService;
import dk.pro3.hateoas_exercise.service.CountryService;
import dk.pro3.hateoas_exercise.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

// Full application with MockMvc in front of it. The "test" profile turns the
// DataSeeder off, so every test starts from the small fixture below. The repositories
// are in memory, so the context is thrown away after each test to reset them.
// Ids come from static counters and keep counting across tests, so tests use the ids
// of the fixture objects instead of hard-coded numbers.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
abstract class ApiTestBase
{
  @Autowired MockMvc mockMvc;
  @Autowired CountryService countryService;
  @Autowired CityService cityService;
  @Autowired TripService tripService;

  Country denmark;
  City copenhagen;
  City aarhus;
  Trip trip;
  Visit copenhagenVisit;   // visit to a city
  Visit denmarkVisit;      // visit to a country

  @BeforeEach void createFixture()
  {
    denmark = countryService.createCountry("Denmark", "DK", Set.of(Continent.EUROPE),
        "Copenhagen", 660_000);
    copenhagen = denmark.getCapital();
    aarhus = cityService.createCity("DK", "Aarhus", 300_000);
    countryService.createCountry("Japan", "JP", Set.of(Continent.ASIA), "Tokyo", 14_000_000);

    trip = tripService.createTrip("Weekend in Denmark", null, copenhagen.getId(),
        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), "Nyhavn");
    copenhagenVisit = trip.getVisits().getFirst();
    denmarkVisit = tripService.addVisit(trip.getId(), "DK", null,
        LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 4), null);
  }
}
