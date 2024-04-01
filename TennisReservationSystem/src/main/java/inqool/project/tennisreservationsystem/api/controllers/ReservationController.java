package inqool.project.tennisreservationsystem.api.controllers;

import inqool.project.tennisreservationsystem.api.model.Reservation;
import inqool.project.tennisreservationsystem.service.ReservationService;
import inqool.project.tennisreservationsystem.service.UserService;
import inqool.project.tennisreservationsystem.api.model.User;
import inqool.project.tennisreservationsystem.api.model.comparator.ReservationCreationComparator;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

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

    @RequestMapping(value = "/api/reservation", method = RequestMethod.POST)
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

    @RequestMapping(value = "/api/court/{id}/reservation", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<TreeSet<Reservation>> getReservationsForCourtId(@PathVariable long id) {
        TreeSet<Reservation> result = new TreeSet<>(new ReservationCreationComparator());
        result.addAll(reservationService.getReservationsByCourtId(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

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

    @RequestMapping(value = "/api/reservation/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Reservation> getReservationById(@PathVariable long id) {
        Reservation reservation = reservationService.getEntity(id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

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

    @RequestMapping(value = "/api/reservation/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Reservation> deleteReservation(@PathVariable long id) {
        Reservation reservation = reservationService.getEntity(id);
        reservationService.softEntityDelete(id);
        reservationTreeSet.remove(reservation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
