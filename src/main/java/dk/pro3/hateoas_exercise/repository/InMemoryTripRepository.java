package dk.pro3.hateoas_exercise.repository;

import dk.pro3.hateoas_exercise.domain.Trip;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository public class InMemoryTripRepository implements TripRepository
{
  // ConcurrentSkipListMap: a map that keeps its keys sorted. A skip list is a sorted
  // linked list with extra "express lanes" on top, so a search can skip most elements
  // (O(log n), like binary search). Sorted keys + ids that only increase means findAll
  // returns trips in creation order. "Concurrent" because the bean is shared by all
  // request threads; it updates only a few pointers per insert, using atomic
  // compare-and-swap instead of locking the whole map.
  private final Map<Long, Trip> trips = new ConcurrentSkipListMap<>();

  @Override public List<Trip> findAll()
  {
    return List.copyOf(trips.values());
  }

  @Override public Optional<Trip> findById(Long id)
  {
    return Optional.ofNullable(trips.get(id));
  }

  @Override public List<Trip> findByName(String name)
  {
    return trips.values().stream()
        .filter(trip -> trip.getName().equalsIgnoreCase(name)).toList();
  }

  @Override public boolean existsById(Long id)
  {
    return trips.containsKey(id);
  }

  @Override public Trip save(
      Trip trip)   // ids come from the Trip constructor, so save is just put
  {
    trips.put(trip.getId(), trip);
    return trip;
  }

  @Override public void deleteById(Long id)
  {
    trips.remove(id);
  }
}
