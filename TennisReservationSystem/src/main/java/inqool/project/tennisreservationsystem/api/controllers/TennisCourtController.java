package inqool.project.tennisreservationsystem.api.controllers;

import inqool.project.tennisreservationsystem.service.CourtTypeService;
import inqool.project.tennisreservationsystem.service.TennisCourtService;
import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller class for REST api of TennisCourt
 *
 * @author Boris Lukačovič
 */
@RestController
public class TennisCourtController {

    private final TennisCourtService tennisCourtService;
    private final CourtTypeService courtTypeService;

    @Autowired
    public TennisCourtController(TennisCourtService tennisCourtService, CourtTypeService courtTypeService) {
        this.tennisCourtService = tennisCourtService;
        this.courtTypeService = courtTypeService;
    }

    /**
     * The method is responsible for the creation of a tennisCourt with given courtTypeId
     *
     * @param courtTypeId id of the courtType for the tennisCourt to be created with
     * @return the newly created tennisCourt
     */
    @RequestMapping(value = "/api/court", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TennisCourt> insertNewTennisCourt(@RequestParam long courtTypeId) {
        CourtType courtType = courtTypeService.getEntity(courtTypeId);
        if (courtType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        TennisCourt tennisCourt = tennisCourtService.insertEntity(new TennisCourt(courtType));
        return new ResponseEntity<>(tennisCourt, HttpStatus.CREATED);
    }

    /**
     * @param id represents an id of a tennisCourt
     * @return the tennisCourt belonging to given tennisCourtId
     */
    @RequestMapping(value = "api/court/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<TennisCourt> getTennisCourtById(@PathVariable long id) {
        return new ResponseEntity<>(tennisCourtService.getEntity(id), HttpStatus.OK);
    }

    /**
     * @return all the tennisCourts in the database
     */
    @RequestMapping(value = "api/courts", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TennisCourt>> getAllTennisCourts() {
        return new ResponseEntity<>(tennisCourtService.getAllEntities(), HttpStatus.OK);
    }

    /**
     * The method is responsible for updating of tennisCourts
     *
     * @param id represents the id of a tennisCourt to be updated
     * @param courtTypeId represents the new courtType of the tennisCourt
     * @return the newly updated tennisCourt
     */
    @RequestMapping(value = "/api/court/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TennisCourt> updateTennisCourt(@PathVariable long id, @RequestParam long courtTypeId) {
        CourtType courtType = courtTypeService.getEntity(courtTypeId);
        TennisCourt tennisCourt = tennisCourtService.getEntity(id);
        if (courtType == null || tennisCourt == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        tennisCourt.setCourtType(courtType);
        return new ResponseEntity<>(tennisCourtService.updateEntity(tennisCourt), HttpStatus.OK);
    }

    /**
     * The method is responsible for the soft-deletion of a tennisCourt
     *
     * @param id represents the id of the tennisCourt to be deleted
     * @return no contend HttpStatus code
     */
    @RequestMapping(value = "api/court/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<TennisCourt> deleteTennisCourt(@PathVariable long id) {
        tennisCourtService.softEntityDelete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
