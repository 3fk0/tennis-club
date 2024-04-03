package inqool.project.tennisreservationsystem.api.controllers;

import inqool.project.tennisreservationsystem.api.model.Reservation;
import inqool.project.tennisreservationsystem.api.model.comparator.ReservationCreationComparator;
import inqool.project.tennisreservationsystem.service.ReservationService;
import inqool.project.tennisreservationsystem.service.UserService;
import inqool.project.tennisreservationsystem.api.model.User;
import inqool.project.tennisreservationsystem.api.model.comparator.ReservationIntervalComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

/**
 * Controller class for REST api of the Reservation class.
 *
 * @author Boris Lukačovič
 */

@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    private final TreeSet<Reservation> reservationTreeSet;

    @Autowired
    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;

        reservationTreeSet = new TreeSet<>(new ReservationIntervalComparator());
        reservationTreeSet.addAll(reservationService.getAllEntities());
    }

    /**
     * The method is responsible for validation of a Reservation.
     * The validation process has 3 stages:
     * 1) The phone number is checked for valid pattern.
     * 2) Names of the users with same phone number are checked.
     * 3) The interval of the new Reservation is checked against all others.
     * <p>
     * Once the validation is complete the Reservation gets inserted into the database.
     * If the user doesn't exist, they will be created.
     *
     * @param reservation represents the reservation to be inserted
     * @return the cost of the inserted reservation
     */
    @RequestMapping(value = "/api/reservation", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Float> insertNewReservation(@RequestBody Reservation reservation) {
        if (!reservation.getUser().getTelephoneNumber().matches("^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The phone number is not valid");
        }

        User user =  userService.getEntity(reservation.getUser().getTelephoneNumber());
        if (user == null) {
            userService.insertEntity(reservation.getUser());
        } else if (!user.getName().equals(reservation.getUser().getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already in use");
        }

        if (reservationTreeSet.contains(reservation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation interval already taken");
        }

        Reservation result = reservationService.insertEntity(reservation);
        reservationTreeSet.add(result);

        return new ResponseEntity<>(result.getPrice(), HttpStatus.CREATED);
    }

    /**
     * The method is responsible for the retrieval of all reservations on a given court with its id.
     *
     * @param id the id of the court
     * @return list of reservations which are returned in ascending order based
     *         on Reservation creation time
     */
    @RequestMapping(value = "/api/court/{id}/reservation", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<TreeSet<Reservation>> getReservationsForCourtId(@PathVariable long id) {
        TreeSet<Reservation> result = new TreeSet<>(new ReservationCreationComparator());
        result.addAll(reservationService.getReservationsByCourtId(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * The function is responsible for the retrieval of all reservations of given phoneNumber
     *
     * @param phoneNumber represents the phoneNumber under which the system registered the reservations
     * @param onlyFuture if set to true, the method filters out all reservations started in the past
     * @return list of Reservations for given phoneNumber
     */
    @RequestMapping(value = "/api/reservation", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Reservation>> getReservationsForPhoneNumber(@RequestParam String phoneNumber, @RequestParam Boolean onlyFuture) {
        List<Reservation> result = reservationService.getReservationsByPhoneNumber(phoneNumber);

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        if (onlyFuture != null && onlyFuture) {
            result = result.stream()
                    .filter(reservation -> reservation.getReservationStart().isAfter(localDateTimeNow))
                    .toList();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Generic getter of reservations based on their id.
     *
     * @param id of the reservation
     * @return the Reservation of given id
     */
    @RequestMapping(value = "/api/reservation/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Reservation> getReservationById(@PathVariable long id) {
        Reservation reservation = reservationService.getEntity(id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    /**
     * The method is responsible for updating existing Reservations. Similarly to inserting,
     * the method checks for interval violation and user existence.
     * If the user doesn't exist, they will be created.
     *
     * @param reservation represents the reservation to be updated
     * @return the updated Reservation
     */
    @RequestMapping(value = "/api/reservation", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation) {
        Reservation oldReservation = reservationService.getEntity(reservation.getId());
        reservationTreeSet.remove(oldReservation);

        if (reservationTreeSet.contains(reservation)) {
            reservationTreeSet.add(oldReservation);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation interval already taken");
        }

        reservationTreeSet.add(reservation);

        User user = reservation.getUser();
        if (!userService.hasEntity(user.getTelephoneNumber())) {
            userService.insertEntity(user);
        }

        return new ResponseEntity<>(reservationService.updateEntity(reservation), HttpStatus.OK);
    }

    /**
     * The method is responsible for the soft-deletion of a Reservation with given id.
     *
     * @param id represents the id of the Reservation to be deleted
     * @return no content HttpStatus code
     */
    @RequestMapping(value = "/api/reservation/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public ResponseEntity<Reservation> deleteReservation(@PathVariable long id) {
        Reservation reservation = reservationService.getEntity(id);
        reservationService.softEntityDelete(id);
        reservationTreeSet.remove(reservation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
