package dk.pro3.hateoas_exercise.domain;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryTest
{
  private Country denmark()
  {
    return Country.create("Denmark", "DK", Set.of(Continent.EUROPE), "Copenhagen", 660_000);
  }

  @Test void createMakesTheCapitalACityOfTheCountry()
  {
    Country denmark = denmark();

    assertThat(denmark.getCapital().getName()).isEqualTo("Copenhagen");
    assertThat(denmark.getCities()).containsExactly(denmark.getCapital());
    assertThat(denmark.getCapital().isCapital()).isTrue();
  }

  @Test void unknownCountryCodeIsRejected()
  {
    assertThatThrownBy(() -> Country.create("Nowhere", "XX", Set.of(Continent.EUROPE), "Capital", 1))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void countryWithoutContinentsIsRejected()
  {
    assertThatThrownBy(() -> Country.create("Denmark", "DK", Set.of(), "Copenhagen", 1))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void capitalCannotBeRemoved()
  {
    Country denmark = denmark();

    assertThatThrownBy(() -> denmark.removeCity(denmark.getCapital()))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test void otherCitiesCanBeRemoved()
  {
    Country denmark = denmark();
    City aarhus = new City("Aarhus", denmark, 300_000);

    denmark.removeCity(aarhus);

    assertThat(denmark.getCities()).doesNotContain(aarhus);
  }

  @Test void capitalCanMoveToAnotherCityOfTheCountry()
  {
    Country denmark = denmark();
    City aarhus = new City("Aarhus", denmark, 300_000);

    denmark.setCapital(aarhus);

    assertThat(denmark.getCapital()).isSameAs(aarhus);
  }

  @Test void capitalCannotBeACityOfAnotherCountry()
  {
    Country denmark = denmark();
    Country sweden = Country.create("Sweden", "SE", Set.of(Continent.EUROPE), "Stockholm", 980_000);

    assertThatThrownBy(() -> denmark.setCapital(sweden.getCapital()))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
