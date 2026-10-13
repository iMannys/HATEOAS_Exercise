package dk.pro3.hateoas_exercise.controller;

import dk.pro3.hateoas_exercise.assembler.TripModelAssembler;
import dk.pro3.hateoas_exercise.assembler.VisitModelAssembler;
import dk.pro3.hateoas_exercise.assembler.model.TripModel;
import dk.pro3.hateoas_exercise.assembler.model.VisitModel;
import dk.pro3.hateoas_exercise.controller.request.AddVisitRequest;
import dk.pro3.hateoas_exercise.controller.request.CreateTripRequest;
import dk.pro3.hateoas_exercise.controller.request.RenameTripRequest;
import dk.pro3.hateoas_exercise.service.TripService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TripController
{
  private final TripModelAssembler tripModelAssembler;
  private final VisitModelAssembler visitModelAssembler;
  private final TripService tripService;

  public TripController(TripModelAssembler tripModelAssembler,
      VisitModelAssembler visitModelAssembler, TripService tripService)
  {
    this.tripModelAssembler = tripModelAssembler;
    this.visitModelAssembler = visitModelAssembler;
    this.tripService = tripService;
  }

  @GetMapping("/trip")
  public ResponseEntity<CollectionModel<TripModel>> getTrips()
  {
    return ResponseEntity.ok(tripModelAssembler.toCollectionModel(tripService.getAllTrips()).add(linkTo(methodOn(
        TripController.class).getTrips()).withSelfRel()));
  }

  @GetMapping("/trip/{id}")
  public ResponseEntity<TripModel> getTrip(@PathVariable Long id)
  {
    return ResponseEntity.ok(tripModelAssembler.toModel(tripService.getTrip(id)));
  }

  @GetMapping(value = "/trip", params = "name")
  public ResponseEntity<CollectionModel<TripModel>> getTripsByName(@RequestParam String name)
  {
    return ResponseEntity.ok(tripModelAssembler.toCollectionModel(
        tripService.getTripsByName(name)).add(linkTo(methodOn(TripController.class).getTripsByName(name)).withSelfRel()));
  }

  // The trip a visit belongs to. Lives here, not in VisitController, because it
  // returns a trip (same as /country/{code}/cities living in CityController).
  @GetMapping("/visit/{id}/trip")
  public ResponseEntity<TripModel> getTripOfVisit(@PathVariable Long id)
  {
    return ResponseEntity.ok(tripModelAssembler.toModel(tripService.getTripOfVisit(id)));
  }

  @GetMapping("/trip/{id}/visits")
  public ResponseEntity<CollectionModel<VisitModel>> getVisitsOfTrip(@PathVariable Long id)
  {
    return ResponseEntity.ok(visitModelAssembler.toCollectionModel(
        tripService.getTrip(id).getVisits()).add(linkTo(methodOn(TripController.class).getVisitsOfTrip(
        id)).withSelfRel()));
  }

  @PostMapping("/trip")
  public ResponseEntity<TripModel> createTrip(
      @Valid @RequestBody CreateTripRequest createTripRequest)
  {
    TripModel tripModel = tripModelAssembler.toModel(
        tripService.createTrip(createTripRequest.name(), createTripRequest.countryCode(),
            createTripRequest.cityId(), createTripRequest.arrivalDate(),
            createTripRequest.departureDate(), createTripRequest.journal()));
    return ResponseEntity.created(
            tripModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(tripModel);
  }

  @PatchMapping("/trip/{id}")
  public ResponseEntity<TripModel> renameTrip(@PathVariable Long id,
      @Valid @RequestBody RenameTripRequest renameTripRequest)
  {
    return ResponseEntity.ok(tripModelAssembler.toModel(
        tripService.renameTrip(id, renameTripRequest.name())));
  }

  // Visits are created through their trip (the aggregate root), so the new visit's
  // own URL is /visit/{id}, served by VisitController.
  @PostMapping("/trip/{id}/visits")
  public ResponseEntity<VisitModel> addVisit(@PathVariable Long id,
      @Valid @RequestBody AddVisitRequest addVisitRequest)
  {
    VisitModel visitModel = visitModelAssembler.toModel(
        tripService.addVisit(id, addVisitRequest.countryCode(), addVisitRequest.cityId(),
            addVisitRequest.arrivalDate(), addVisitRequest.departureDate(),
            addVisitRequest.journal()));
    return ResponseEntity.created(
            visitModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(visitModel);
  }

  @DeleteMapping("/trip/{id}/visits/{visitId}")
  public ResponseEntity<Void> removeVisit(@PathVariable Long id, @PathVariable Long visitId)
  {
    tripService.removeVisit(id, visitId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/trip/{id}")
  public ResponseEntity<Void> deleteTrip(@PathVariable Long id)
  {
    tripService.deleteTrip(id);
    return ResponseEntity.noContent().build();
  }
}
