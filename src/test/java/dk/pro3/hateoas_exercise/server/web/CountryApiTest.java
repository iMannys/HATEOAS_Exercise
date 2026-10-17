package dk.pro3.hateoas_exercise.server.web;

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

class CountryApiTest extends ApiTestBase
{
  @Test void rootLinksToEveryCollection() throws Exception
  {
    mockMvc.perform(get("/api"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/api")))
        .andExpect(jsonPath("$._links.countries.href").value(endsWith("/api/country")))
        .andExpect(jsonPath("$._links.cities.href").value(endsWith("/api/city")))
        .andExpect(jsonPath("$._links.visits.href").value(endsWith("/api/visit")))
        .andExpect(jsonPath("$._links.trips.href").value(endsWith("/api/trip")));
  }

  @Test void countryHasItsDataAndLinks() throws Exception
  {
    mockMvc.perform(get("/api/country/DK"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Denmark"))
        .andExpect(jsonPath("$.code").value("DK"))
        .andExpect(jsonPath("$.continents[0]").value("EUROPE"))
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/country/DK")))
        .andExpect(jsonPath("$._links.countries.href").value(endsWith("/country")))
        .andExpect(jsonPath("$._links.cities.href").value(endsWith("/country/DK/cities")))
        .andExpect(jsonPath("$._links.visits.href").value(endsWith("/visit?country=DK")))
        .andExpect(jsonPath("$._links.capital.href").value(endsWith("/city/" + copenhagen.getId())));
  }

  @Test void countriesAreEmbeddedUnderTheirCollectionRelation() throws Exception
  {
    mockMvc.perform(get("/api/country"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.countries", hasSize(2)))
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/country")));
  }

  @Test void unknownCountryIsNotFound() throws Exception
  {
    mockMvc.perform(get("/api/country/SE"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.detail").exists());
  }

  @Test void countriesCanBeFilteredByContinentCodeOrName() throws Exception
  {
    for (String continent : new String[] {"AS", "as", "ASIA"})
    {
      mockMvc.perform(get("/api/country").param("continent", continent))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$._embedded.countries[*].code").value(containsInAnyOrder("JP")));
    }
  }

  @Test void unknownContinentGivesBadRequestWithTheValidCodes() throws Exception
  {
    mockMvc.perform(get("/api/country").param("continent", "XX"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(containsString("Valid codes: AF, AN, AS, EU, NA, OC, SA")));
  }

  @Test void createdCountryIsReturnedWithItsLocation() throws Exception
  {
    mockMvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content("""
            {"name": "Sweden", "code": "SE", "continents": ["EUROPE"],
             "capitalName": "Stockholm", "capitalPopulation": 980000}
            """))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", endsWith("/country/SE")))
        .andExpect(jsonPath("$.name").value("Sweden"));

    mockMvc.perform(get("/api/country/SE")).andExpect(status().isOk());
  }

  @Test void duplicateCountryIsAConflict() throws Exception
  {
    mockMvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content("""
            {"name": "Denmark", "code": "DK", "continents": ["EUROPE"],
             "capitalName": "Copenhagen", "capitalPopulation": 1}
            """))
        .andExpect(status().isConflict());
  }

  @Test void invalidCountryListsTheInvalidFields() throws Exception
  {
    mockMvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content("""
            {"name": "", "code": "SE", "continents": ["EUROPE"], "capitalName": "Stockholm"}
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[*].field").value(containsInAnyOrder("name", "capitalPopulation")));
  }

  @Test void capitalCanBeChanged() throws Exception
  {
    mockMvc.perform(patch("/api/country/DK").contentType(MediaType.APPLICATION_JSON)
            .content("{\"cityId\": " + aarhus.getId() + "}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.capital.href").value(endsWith("/city/" + aarhus.getId())));
  }

  @Test void countryWithVisitsCannotBeDeleted() throws Exception
  {
    mockMvc.perform(delete("/api/country/DK")).andExpect(status().isConflict());
  }

  @Test void countryWithoutVisitsCanBeDeleted() throws Exception
  {
    mockMvc.perform(delete("/api/country/JP")).andExpect(status().isNoContent());
    mockMvc.perform(get("/api/country/JP")).andExpect(status().isNotFound());
  }
}
