package dk.pro3.hateoas_exercise.domain;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisitTest
{
  private static final LocalDate MAY_1 = LocalDate.of(2026, 5, 1);
  private static final LocalDate MAY_3 = LocalDate.of(2026, 5, 3);

  private final Country denmark = Country.create("Denmark", "DK", Set.of(Continent.EUROPE),
      "Copenhagen", 660_000);

  @Test void visitToACityBelongsToTheCitysCountry()
  {
    Visit visit = new Visit(denmark.getCapital(), MAY_1, MAY_3, null);

    assertThat(visit.getCountry()).isSameAs(denmark);
  }

  @Test void visitToACountryBelongsToThatCountry()
  {
    Visit visit = new Visit(denmark, MAY_1, MAY_3, null);

    assertThat(visit.getCountry()).isSameAs(denmark);
  }

  @Test void visitNeedsAPlace()
  {
    assertThatThrownBy(() -> new Visit(null, MAY_1, MAY_3, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void departureCannotBeBeforeArrival()
  {
    assertThatThrownBy(() -> new Visit(denmark, MAY_3, MAY_1, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void sameDayVisitIsAllowed()
  {
    Visit visit = new Visit(denmark, MAY_1, MAY_1, null);

    assertThat(visit.getDepartureDate()).isEqualTo(MAY_1);
  }

  @Test void failedDateChangeKeepsTheOldDates()
  {
    Visit visit = new Visit(denmark, MAY_1, MAY_3, null);

    assertThatThrownBy(() -> visit.changeDates(MAY_3, MAY_1))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(visit.getArrivalDate()).isEqualTo(MAY_1);
    assertThat(visit.getDepartureDate()).isEqualTo(MAY_3);
  }
}
