package dk.pro3.hateoas_exercise.domain;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CityTest
{
  private final Country denmark = Country.create("Denmark", "DK", Set.of(Continent.EUROPE),
      "Copenhagen", 660_000);

  @Test void newCityRegistersWithItsCountry()
  {
    City aarhus = new City("Aarhus", denmark, 300_000);

    assertThat(denmark.getCities()).contains(aarhus);
    assertThat(aarhus.getCountry()).isSameAs(denmark);
    assertThat(aarhus.isCapital()).isFalse();
  }

  @Test void cityNeedsACountry()
  {
    assertThatThrownBy(() -> new City("Aarhus", null, 300_000))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void cityNeedsAName()
  {
    assertThatThrownBy(() -> new City(" ", denmark, 300_000))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void populationCannotBeNegative()
  {
    City aarhus = new City("Aarhus", denmark, 300_000);

    assertThatThrownBy(() -> aarhus.setPopulation(-1))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(aarhus.getPopulation()).isEqualTo(300_000);
  }

  @Test void citiesGetDifferentIds()
  {
    City aarhus = new City("Aarhus", denmark, 300_000);
    City odense = new City("Odense", denmark, 180_000);

    assertThat(aarhus.getId()).isNotEqualTo(odense.getId());
  }
}
