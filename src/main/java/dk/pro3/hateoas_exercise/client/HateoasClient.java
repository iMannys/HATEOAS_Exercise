package dk.pro3.hateoas_exercise.client;

import dk.pro3.hateoas_exercise.client.dto.CityDTO;
import dk.pro3.hateoas_exercise.client.dto.CountryDTO;
import dk.pro3.hateoas_exercise.client.dto.TripDTO;
import dk.pro3.hateoas_exercise.client.dto.VisitDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.web.client.RestClientException;

import java.net.URI;

// A HATEOAS client: the only URL it knows is the root. Every other URL is found by
// following links by their rel name, so the server can change its URLs freely.
public class HateoasClient
{
  private static final String ROOT_URL = "http://localhost:8080/";

  // The type references end in {} so they are anonymous subclasses: a subclass keeps
  // its generic type at runtime, so Jackson knows to build CountryDTOs and not maps.
  private static final TypeReferences.CollectionModelType<EntityModel<CountryDTO>> COUNTRIES =
      new TypeReferences.CollectionModelType<>() {};
  private static final TypeReferences.CollectionModelType<EntityModel<CityDTO>> CITIES =
      new TypeReferences.CollectionModelType<>() {};
  private static final TypeReferences.EntityModelType<CityDTO> CITY =
      new TypeReferences.EntityModelType<>() {};
  private static final TypeReferences.CollectionModelType<EntityModel<TripDTO>> TRIPS =
      new TypeReferences.CollectionModelType<>() {};
  private static final TypeReferences.CollectionModelType<EntityModel<VisitDTO>> VISITS =
      new TypeReferences.CollectionModelType<>() {};

  public static void main(String[] args)
  {
    try
    {
      new HateoasClient().run();
    }
    catch (RestClientException e)
    {
      System.out.println("*** Could not talk to the server at " + ROOT_URL + " ***");
      System.out.println(e.getMessage());
    }
  }

  private void run()
  {
    printCountries();
    printTrips();
  }

  // root -> countries -> (each country) -> capital and cities
  private void printCountries()
  {
    System.out.println("=== Countries ===");
    for (EntityModel<CountryDTO> country : fromRoot("countries", COUNTRIES).getContent())
    {
      CountryDTO dto = country.getContent();
      System.out.println(dto.name() + " (" + dto.code() + "), " + String.join(", ", dto.continents()));

      CityDTO capital = follow(country.getRequiredLink("capital"), CITY).getContent();
      System.out.println("  Capital: " + capital.name());

      for (EntityModel<CityDTO> city : follow(country.getRequiredLink("cities"), CITIES).getContent())
      {
        System.out.println("  City: " + city.getContent().name() + ", population "
            + city.getContent().population());
      }
    }
  }

  // root -> trips -> (each trip) -> visits
  private void printTrips()
  {
    System.out.println();
    System.out.println("=== Trips ===");
    for (EntityModel<TripDTO> trip : fromRoot("trips", TRIPS).getContent())
    {
      TripDTO dto = trip.getContent();
      System.out.println(dto.name() + ": " + dto.startDate() + " to " + dto.endDate());

      for (EntityModel<VisitDTO> visit : follow(trip.getRequiredLink("visits"), VISITS).getContent())
      {
        VisitDTO visitDto = visit.getContent();
        String journal = visitDto.journal() == null ? "" : " - " + visitDto.journal();
        System.out.println("  " + visitDto.arrivalDate() + " to " + visitDto.departureDate()
            + ": " + visitDto.placeName() + journal);
      }
    }
  }

  // Starts at the root and follows one link by its rel name
  private <T> CollectionModel<T> fromRoot(String rel, TypeReferences.CollectionModelType<T> type)
  {
    return new Traverson(URI.create(ROOT_URL), MediaTypes.HAL_JSON).follow(rel).toObject(type);
  }

  // Fetches what a link from an earlier response points to
  private <T> CollectionModel<T> follow(Link link, TypeReferences.CollectionModelType<T> type)
  {
    return new Traverson(URI.create(link.getHref()), MediaTypes.HAL_JSON).follow().toObject(type);
  }

  private <T> EntityModel<T> follow(Link link, TypeReferences.EntityModelType<T> type)
  {
    return new Traverson(URI.create(link.getHref()), MediaTypes.HAL_JSON).follow().toObject(type);
  }
}
