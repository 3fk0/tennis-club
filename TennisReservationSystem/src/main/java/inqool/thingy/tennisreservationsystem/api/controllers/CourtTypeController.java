package inqool.thingy.tennisreservationsystem.api.controllers;

import inqool.thingy.tennisreservationsystem.api.model.CourtType;
import inqool.thingy.tennisreservationsystem.service.CourtTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourtTypeController {

    private final CourtTypeService courtTypeService;

    @Autowired
    public CourtTypeController(CourtTypeService courtTypeService) {
        this.courtTypeService = courtTypeService;
    }

    @RequestMapping(value = "/api/courtTypes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<CourtType>> getAllCourtTypes() {
        return new ResponseEntity<>(courtTypeService.getAllEntities(), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/courtType/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CourtType> getCourtType(@PathVariable long id) {
        return new ResponseEntity<>(courtTypeService.getEntity(id), HttpStatus.OK);
    }
}
