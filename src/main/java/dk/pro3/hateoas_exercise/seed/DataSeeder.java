package dk.pro3.hateoas_exercise.seed;

import dk.pro3.hateoas_exercise.domain.City;
import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.Trip;
import dk.pro3.hateoas_exercise.domain.enums.Continent;
import dk.pro3.hateoas_exercise.service.CityService;
import dk.pro3.hateoas_exercise.service.CountryService;
import dk.pro3.hateoas_exercise.service.TripService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

// Fills the in-memory repositories with sample data at startup, so the API has
// something to return. Goes through the services, so the same rules apply as for
// requests. Only runs in the "dev" profile (the default, see application.properties).
@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner
{
  private final CountryService countryService;
  private final CityService cityService;
  private final TripService tripService;

  public DataSeeder(CountryService countryService, CityService cityService,
      TripService tripService)
  {
    this.countryService = countryService;
    this.cityService = cityService;
    this.tripService = tripService;
  }

  @Override public void run(String... args)
  {
    Country denmark = countryService.createCountry("Denmark", "DK",
        Set.of(Continent.EUROPE), "Copenhagen", 660_000);
    City aarhus = cityService.createCity("DK", "Aarhus", 300_000);
    cityService.createCity("DK", "Odense", 180_000);

    Country japan = countryService.createCountry("Japan", "JP",
        Set.of(Continent.ASIA), "Tokyo", 14_000_000);
    City kyoto = cityService.createCity("JP", "Kyoto", 1_460_000);

    countryService.createCountry("Turkey", "TR",
        Set.of(Continent.EUROPE, Continent.ASIA), "Ankara", 5_700_000);
    cityService.createCity("TR", "Istanbul", 15_600_000);

    Trip denmarkTrip = tripService.createTrip("Weekend in Denmark", null,
        denmark.getCapital().getId(), LocalDate.of(2026, 5, 1),
        LocalDate.of(2026, 5, 3), "Nyhavn and Tivoli");
    tripService.addVisit(denmarkTrip.getId(), null, aarhus.getId(),
        LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 4), "ARoS");

    Trip japanTrip = tripService.createTrip("Japan round trip", null,
        japan.getCapital().getId(), LocalDate.of(2026, 9, 10),
        LocalDate.of(2026, 9, 15), null);
    tripService.addVisit(japanTrip.getId(), null, kyoto.getId(),
        LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 20), "Temples");
    tripService.addVisit(japanTrip.getId(), "JP", null,
        LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 22), "Countryside");
  }
}
