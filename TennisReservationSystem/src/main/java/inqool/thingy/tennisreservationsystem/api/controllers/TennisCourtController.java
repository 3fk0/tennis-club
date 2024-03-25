package inqool.thingy.tennisreservationsystem.api.controllers;

import inqool.thingy.tennisreservationsystem.api.model.CourtType;
import inqool.thingy.tennisreservationsystem.api.model.TennisCourt;
import inqool.thingy.tennisreservationsystem.service.CourtTypeService;
import inqool.thingy.tennisreservationsystem.service.TennisCourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TennisCourtController {

    private final TennisCourtService tennisCourtService;
    private final CourtTypeService courtTypeService;

    @Autowired
    public TennisCourtController(TennisCourtService tennisCourtService, CourtTypeService courtTypeService) {
        this.tennisCourtService = tennisCourtService;
        this.courtTypeService = courtTypeService;
    }

    @RequestMapping(value = "/api/court", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TennisCourt> insertNewTennisCourt(@RequestParam int courtTypeCode) {
        Optional<CourtType> optionalCourtType = courtTypeService.getEntity(courtTypeCode);
        if (optionalCourtType.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CourtType courtType = optionalCourtType.get();
        TennisCourt tennisCourt = tennisCourtService.insertEntity(new TennisCourt(courtType));

        return new ResponseEntity<>(tennisCourt, HttpStatus.CREATED);
    }

    @RequestMapping(value = "api/court/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<TennisCourt> getTennisCourtById(@PathVariable long id) {
        Optional<TennisCourt> tennisCourtOptional = tennisCourtService.getEntity(id);
        return tennisCourtOptional.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(tennisCourtOptional.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "api/court/{id}")
    @ResponseBody
    public ResponseEntity<TennisCourt> deleteTennisCourt(@PathVariable long id) {
        tennisCourtService.softEntityDelete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
