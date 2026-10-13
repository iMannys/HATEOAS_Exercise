package dk.pro3.hateoas_exercise.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VisitAndTripApiTest extends ApiTestBase
{
  // --- visits ---

  @Test void visitHasItsDataAndLinks() throws Exception
  {
    Long id = copenhagenVisit.getId();

    mockMvc.perform(get("/visit/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.arrivalDate").value("2026-05-01"))
        .andExpect(jsonPath("$.departureDate").value("2026-05-03"))
        .andExpect(jsonPath("$.journal").value("Nyhavn"))
        .andExpect(jsonPath("$.placeName").value("Copenhagen"))
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/visit/" + id)))
        .andExpect(jsonPath("$._links.visits.href").value(endsWith("/visit")))
        .andExpect(jsonPath("$._links.trip.href").value(endsWith("/visit/" + id + "/trip")))
        .andExpect(jsonPath("$._links.country.href").value(endsWith("/country/DK")))
        .andExpect(jsonPath("$._links.visitsByCountry.href").value(endsWith("/visit?country=DK")))
        .andExpect(jsonPath("$._links.changeDates.href").value(endsWith("/visit/" + id + "/dates")))
        .andExpect(jsonPath("$._links.updateJournal.href").value(endsWith("/visit/" + id + "/journal")));
  }

  @Test void placeLinkOfACityVisitPointsToTheCity() throws Exception
  {
    mockMvc.perform(get("/visit/" + copenhagenVisit.getId()))
        .andExpect(jsonPath("$._links.place.href").value(endsWith("/city/" + copenhagen.getId())));
  }

  @Test void placeLinkOfACountryVisitPointsToTheCountry() throws Exception
  {
    mockMvc.perform(get("/visit/" + denmarkVisit.getId()))
        .andExpect(jsonPath("$._links.place.href").value(endsWith("/country/DK")));
  }

  @Test void unknownVisitIsNotFound() throws Exception
  {
    mockMvc.perform(get("/visit/-1")).andExpect(status().isNotFound());
  }

  @Test void visitsCanBeFilteredByCountry() throws Exception
  {
    mockMvc.perform(get("/visit").param("country", "DK"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.visits", hasSize(2)));
    mockMvc.perform(get("/visit").param("country", "JP"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded").doesNotExist());
  }

  @Test void datesCanBeChanged() throws Exception
  {
    mockMvc.perform(patch("/visit/" + copenhagenVisit.getId() + "/dates")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"arrivalDate\": \"2026-06-01\", \"departureDate\": \"2026-06-02\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.arrivalDate").value("2026-06-01"))
        .andExpect(jsonPath("$.departureDate").value("2026-06-02"));
  }

  @Test void departureBeforeArrivalIsABadRequest() throws Exception
  {
    mockMvc.perform(patch("/visit/" + copenhagenVisit.getId() + "/dates")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"arrivalDate\": \"2026-06-02\", \"departureDate\": \"2026-06-01\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test void journalCanBeUpdated() throws Exception
  {
    mockMvc.perform(patch("/visit/" + copenhagenVisit.getId() + "/journal")
            .contentType(MediaType.APPLICATION_JSON).content("{\"journal\": \"Tivoli\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.journal").value("Tivoli"));
  }

  // --- trips ---

  @Test void tripHasItsDataAndLinks() throws Exception
  {
    Long id = trip.getId();

    mockMvc.perform(get("/trip/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Weekend in Denmark"))
        .andExpect(jsonPath("$.startDate").value("2026-05-01"))
        .andExpect(jsonPath("$.endDate").value("2026-05-04"))
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/trip/" + id)))
        .andExpect(jsonPath("$._links.trips.href").value(endsWith("/trip")))
        .andExpect(jsonPath("$._links.visits.href").value(endsWith("/trip/" + id + "/visits")));
  }

  // HalConfig renders this rel as an array even when it holds a single link
  @Test void visitedCountriesIsAnArrayEvenWithOneCountry() throws Exception
  {
    mockMvc.perform(get("/trip/" + trip.getId()))
        .andExpect(jsonPath("$._links.visitedCountries", hasSize(1)))
        .andExpect(jsonPath("$._links.visitedCountries[0].href").value(endsWith("/country/DK")));
  }

  @Test void tripOfAVisitIsFoundThroughTheVisitsTripLink() throws Exception
  {
    mockMvc.perform(get("/visit/" + denmarkVisit.getId() + "/trip"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Weekend in Denmark"))
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/trip/" + trip.getId())));
  }

  @Test void tripOfUnknownVisitIsNotFound() throws Exception
  {
    mockMvc.perform(get("/visit/-1/trip")).andExpect(status().isNotFound());
  }

  @Test void visitsOfATripAreListed() throws Exception
  {
    mockMvc.perform(get("/trip/" + trip.getId() + "/visits"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.visits[*].placeName").value(containsInAnyOrder("Copenhagen", "Denmark")));
  }

  @Test void tripsCanBeSearchedByName() throws Exception
  {
    mockMvc.perform(get("/trip").param("name", "weekend in denmark"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.trips", hasSize(1)));
  }

  @Test void createdTripIsReturnedWithItsLocation() throws Exception
  {
    mockMvc.perform(post("/trip").contentType(MediaType.APPLICATION_JSON).content("""
            {"name": "Tokyo", "countryCode": "JP",
             "arrivalDate": "2026-09-10", "departureDate": "2026-09-15"}
            """))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString("/trip/")))
        .andExpect(jsonPath("$.name").value("Tokyo"))
        .andExpect(jsonPath("$._links.visitedCountries[0].href").value(endsWith("/country/JP")));
  }

  @Test void tripWithBothCountryAndCityIsABadRequest() throws Exception
  {
    mockMvc.perform(post("/trip").contentType(MediaType.APPLICATION_JSON).content("""
            {"name": "Both", "countryCode": "DK", "cityId": %d,
             "arrivalDate": "2026-09-10", "departureDate": "2026-09-15"}
            """.formatted(aarhus.getId())))
        .andExpect(status().isBadRequest());
  }

  @Test void tripWithoutNameIsABadRequest() throws Exception
  {
    mockMvc.perform(post("/trip").contentType(MediaType.APPLICATION_JSON).content("""
            {"countryCode": "DK", "arrivalDate": "2026-09-10", "departureDate": "2026-09-15"}
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("name"));
  }

  @Test void tripCanBeRenamed() throws Exception
  {
    mockMvc.perform(patch("/trip/" + trip.getId()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Long weekend\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Long weekend"));
  }

  @Test void addedVisitIsReturnedWithItsOwnLocation() throws Exception
  {
    mockMvc.perform(post("/trip/" + trip.getId() + "/visits").contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"cityId": %d, "arrivalDate": "2026-05-04", "departureDate": "2026-05-05"}
                """.formatted(aarhus.getId())))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString("/visit/")))
        .andExpect(jsonPath("$.placeName").value("Aarhus"));

    mockMvc.perform(get("/trip/" + trip.getId() + "/visits"))
        .andExpect(jsonPath("$._embedded.visits", hasSize(3)));
  }

  @Test void visitCanBeRemovedFromATrip() throws Exception
  {
    mockMvc.perform(delete("/trip/" + trip.getId() + "/visits/" + denmarkVisit.getId()))
        .andExpect(status().isNoContent());
    mockMvc.perform(get("/visit/" + denmarkVisit.getId())).andExpect(status().isNotFound());
  }

  @Test void lastVisitOfATripCannotBeRemoved() throws Exception
  {
    mockMvc.perform(delete("/trip/" + trip.getId() + "/visits/" + denmarkVisit.getId()));

    mockMvc.perform(delete("/trip/" + trip.getId() + "/visits/" + copenhagenVisit.getId()))
        .andExpect(status().isConflict());
  }

  @Test void deletedTripIsGoneWithItsVisits() throws Exception
  {
    mockMvc.perform(delete("/trip/" + trip.getId())).andExpect(status().isNoContent());

    mockMvc.perform(get("/trip/" + trip.getId())).andExpect(status().isNotFound());
    mockMvc.perform(get("/visit/" + copenhagenVisit.getId())).andExpect(status().isNotFound());
  }
}
