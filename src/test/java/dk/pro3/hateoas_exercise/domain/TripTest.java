package dk.pro3.hateoas_exercise.domain;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TripTest
{
  private final Country denmark = Country.create("Denmark", "DK", Set.of(Continent.EUROPE),
      "Copenhagen", 660_000);
  private final Country sweden = Country.create("Sweden", "SE", Set.of(Continent.EUROPE),
      "Stockholm", 980_000);

  private Visit visit(Place place, int fromDay, int toDay)
  {
    return new Visit(place, LocalDate.of(2026, 5, fromDay), LocalDate.of(2026, 5, toDay), null);
  }

  @Test void datesSpanAllVisits()
  {
    Trip trip = new Trip("Scandinavia", visit(denmark, 3, 5));
    trip.addVisit(visit(sweden, 1, 2));
    trip.addVisit(visit(denmark.getCapital(), 6, 9));

    assertThat(trip.getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
    assertThat(trip.getEndDate()).isEqualTo(LocalDate.of(2026, 5, 9));
  }

  @Test void countriesAreListedOnceEach()
  {
    Trip trip = new Trip("Scandinavia", visit(denmark, 1, 2));
    trip.addVisit(visit(denmark.getCapital(), 2, 3));
    trip.addVisit(visit(sweden, 3, 4));

    assertThat(trip.getCountries()).containsExactlyInAnyOrder(denmark, sweden);
  }

  @Test void tripNeedsAName()
  {
    assertThatThrownBy(() -> new Trip(" ", visit(denmark, 1, 2)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void tripNeedsAFirstVisit()
  {
    assertThatThrownBy(() -> new Trip("Empty", null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void sameVisitCannotBeAddedTwice()
  {
    Visit visit = visit(denmark, 1, 2);
    Trip trip = new Trip("Denmark", visit);

    assertThatThrownBy(() -> trip.addVisit(visit))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test void lastVisitCannotBeRemoved()
  {
    Visit only = visit(denmark, 1, 2);
    Trip trip = new Trip("Denmark", only);

    assertThatThrownBy(() -> trip.removeVisit(only))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test void visitCanBeRemovedWhileOthersRemain()
  {
    Visit first = visit(denmark, 1, 2);
    Visit second = visit(sweden, 2, 3);
    Trip trip = new Trip("Scandinavia", first);
    trip.addVisit(second);

    trip.removeVisit(first);

    assertThat(trip.getVisits()).containsExactly(second);
  }

  @Test void visitOfAnotherTripCannotBeRemoved()
  {
    Trip trip = new Trip("Denmark", visit(denmark, 1, 2));
    trip.addVisit(visit(denmark, 2, 3));

    assertThatThrownBy(() -> trip.removeVisit(visit(sweden, 1, 2)))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
