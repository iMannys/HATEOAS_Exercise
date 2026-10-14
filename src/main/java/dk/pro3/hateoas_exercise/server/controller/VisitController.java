package dk.pro3.hateoas_exercise.server.controller;

import dk.pro3.hateoas_exercise.server.assembler.VisitModelAssembler;
import dk.pro3.hateoas_exercise.server.assembler.model.VisitModel;
import dk.pro3.hateoas_exercise.server.controller.request.ChangeDatesRequest;
import dk.pro3.hateoas_exercise.server.controller.request.UpdateJournalRequest;
import dk.pro3.hateoas_exercise.server.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class VisitController
{
  private final VisitService visitService;
  private final VisitModelAssembler visitModelAssembler;

  public VisitController(VisitService visitService,
      VisitModelAssembler visitModelAssembler)
  {
    this.visitService = visitService;
    this.visitModelAssembler = visitModelAssembler;
  }

  @GetMapping("/visit")
  public ResponseEntity<CollectionModel<VisitModel>> getVisits()
  {
    return ResponseEntity.ok(
        visitModelAssembler.toCollectionModel(visitService.getAllVisits()).add(
            linkTo(methodOn(VisitController.class).getVisits()).withSelfRel()));
  }

  @GetMapping("/visit/{id}")
  public ResponseEntity<VisitModel> getVisit(@PathVariable Long id)
  {
    return ResponseEntity.ok(
        visitModelAssembler.toModel(visitService.getVisit(id)));
  }

  @GetMapping(value = "/visit", params = "country")
  public ResponseEntity<CollectionModel<VisitModel>> getVisitsByCountry(
      @RequestParam String country)
  {
    return ResponseEntity.ok(visitModelAssembler.toCollectionModel(
        visitService.getVisitsByCountry(country)).add(linkTo(
        methodOn(VisitController.class).getVisitsByCountry(
            country)).withSelfRel()));
  }

  @PatchMapping("/visit/{id}/dates")
  public ResponseEntity<VisitModel> changeDates(@PathVariable Long id,
      @Valid @RequestBody ChangeDatesRequest changeDatesRequest)
  {
    return ResponseEntity.ok(visitModelAssembler.toModel(
        visitService.changeDates(id, changeDatesRequest.arrivalDate(),
            changeDatesRequest.departureDate())));
  }

  @PatchMapping("/visit/{id}/journal")
  public ResponseEntity<VisitModel> updateJournal(@PathVariable Long id,
      @Valid @RequestBody UpdateJournalRequest updateJournalRequest)
  {
    return ResponseEntity.ok(visitModelAssembler.toModel(
        visitService.updateJournal(id, updateJournalRequest.journal())));
  }
}
