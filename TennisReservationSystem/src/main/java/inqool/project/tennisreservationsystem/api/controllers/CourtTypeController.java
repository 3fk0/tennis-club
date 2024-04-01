package inqool.project.tennisreservationsystem.api.controllers;

import inqool.project.tennisreservationsystem.service.CourtTypeService;
import inqool.project.tennisreservationsystem.api.model.CourtType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class CourtTypeController {

    private final ArrayList<CourtType> courtTypes;
    private final CourtTypeService courtTypeService;

    @Autowired
    public CourtTypeController(CourtTypeService courtTypeService) {
        this.courtTypeService = courtTypeService;
        courtTypes = new ArrayList<>(courtTypeService.getAllEntities());
    }

    @RequestMapping(value = "/api/courtTypes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<CourtType>> getAllCourtTypes() {
        return new ResponseEntity<>(courtTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/courtType/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CourtType> getCourtType(@PathVariable long id) {
        Optional<CourtType> optionalCourtType = courtTypes.stream()
                .filter(courtType -> courtType.getId() == id)
                .findFirst();

        if (optionalCourtType.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(optionalCourtType.get(), HttpStatus.OK);
    }

    public void reload() {
        courtTypes.clear();
        courtTypes.addAll(courtTypeService.getAllEntities());
    }
}
