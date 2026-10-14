package dk.pro3.hateoas_exercise.server.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CityApiTest extends ApiTestBase
{
  @Test void cityHasItsDataAndLinks() throws Exception
  {
    mockMvc.perform(get("/city/" + copenhagen.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Copenhagen"))
        .andExpect(jsonPath("$.population").value(660_000))
        .andExpect(jsonPath("$.capital").value(true))
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/city/" + copenhagen.getId())))
        .andExpect(jsonPath("$._links.cities.href").value(endsWith("/city")))
        .andExpect(jsonPath("$._links.country.href").value(endsWith("/country/DK")));
  }

  @Test void unknownCityIsNotFound() throws Exception
  {
    mockMvc.perform(get("/city/-1")).andExpect(status().isNotFound());
  }

  @Test void nonNumericIdIsABadRequest() throws Exception
  {
    mockMvc.perform(get("/city/abc")).andExpect(status().isBadRequest());
  }

  @Test void citiesOfACountryAreListed() throws Exception
  {
    mockMvc.perform(get("/country/DK/cities"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.cities[*].name").value(containsInAnyOrder("Copenhagen", "Aarhus")))
        .andExpect(jsonPath("$._links.self.href").value(endsWith("/country/DK/cities")));
  }

  @Test void citiesOfUnknownCountryIsNotFound() throws Exception
  {
    mockMvc.perform(get("/country/SE/cities")).andExpect(status().isNotFound());
  }

  @Test void citiesCanBeSearchedByName() throws Exception
  {
    mockMvc.perform(get("/city").param("name", "aarhus"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.cities[*].name").value(containsInAnyOrder("Aarhus")));
  }

  @Test void createdCityIsReturnedWithItsLocation() throws Exception
  {
    mockMvc.perform(post("/country/DK/cities").contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Odense\", \"population\": 180000}"))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString("/city/")))
        .andExpect(jsonPath("$.name").value("Odense"))
        .andExpect(jsonPath("$.capital").value(false));
  }

  @Test void negativePopulationIsABadRequest() throws Exception
  {
    mockMvc.perform(patch("/city/" + aarhus.getId()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"population\": -1}"))
        .andExpect(status().isBadRequest());
  }

  @Test void populationCanBeUpdated() throws Exception
  {
    mockMvc.perform(patch("/city/" + aarhus.getId()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"population\": 310000}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.population").value(310_000));
  }

  @Test void capitalCannotBeDeleted() throws Exception
  {
    // The capital also has a visit in the fixture, so use a fresh capital without one
    countryService.changeCapital("DK", aarhus.getId());

    mockMvc.perform(delete("/city/" + aarhus.getId())).andExpect(status().isConflict());
  }

  @Test void cityWithVisitsCannotBeDeleted() throws Exception
  {
    countryService.changeCapital("DK", aarhus.getId());

    mockMvc.perform(delete("/city/" + copenhagen.getId())).andExpect(status().isConflict());
  }

  @Test void cityCanBeDeleted() throws Exception
  {
    mockMvc.perform(delete("/city/" + aarhus.getId())).andExpect(status().isNoContent());
    mockMvc.perform(get("/city/" + aarhus.getId())).andExpect(status().isNotFound());
  }
}
