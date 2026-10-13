package dk.pro3.hateoas_exercise.domain;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContinentTest
{
  @Test void findsByCodeIgnoringCase()
  {
    assertThat(Continent.fromSearch("EU")).isEqualTo(Continent.EUROPE);
    assertThat(Continent.fromSearch("eu")).isEqualTo(Continent.EUROPE);
  }

  // Links built with methodOn(...) put the constant name in the URL
  @Test void findsByConstantName()
  {
    assertThat(Continent.fromSearch("NORTH_AMERICA")).isEqualTo(Continent.NORTH_AMERICA);
  }

  @Test void ignoresSurroundingWhitespace()
  {
    assertThat(Continent.fromSearch(" as ")).isEqualTo(Continent.ASIA);
  }

  @Test void unknownSearchListsValidCodes()
  {
    assertThatThrownBy(() -> Continent.fromSearch("XX"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("'XX'")
        .hasMessageContaining("AF, AN, AS, EU, NA, OC, SA");
  }

  @Test void nullSearchIsRejected()
  {
    assertThatThrownBy(() -> Continent.fromSearch(null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
