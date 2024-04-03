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

/**
 * Controller class for REST api of CourtType.
 *
 * @author Boris Lukačovič
 */

@RestController
public class CourtTypeController {

    private final ArrayList<CourtType> courtTypes;
    private final CourtTypeService courtTypeService;

    @Autowired
    public CourtTypeController(CourtTypeService courtTypeService) {
        this.courtTypeService = courtTypeService;
        courtTypes = new ArrayList<>(courtTypeService.getAllEntities());
    }

    /**
     * @return the list of all courtTypes present at the beginning of the application
     *         or after reload() method is called
     */
    @RequestMapping(value = "/api/courtTypes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<CourtType>> getAllCourtTypes() {
        return new ResponseEntity<>(courtTypes, HttpStatus.OK);
    }

    /**
     * @param id represents the id of the courtType to be returned
     * @return the courtType with given id
     */
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

    /**
     * The method is responsible for updating the actual courtTypes from the database
     */
    public void reload() {
        courtTypes.clear();
        courtTypes.addAll(courtTypeService.getAllEntities());
    }
}
