package inqool.thingy.tennisreservationsystem.api.controllers;

import inqool.thingy.tennisreservationsystem.api.model.User;
import inqool.thingy.tennisreservationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<User>> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<User> insertUser(@RequestParam String name) {
        User newUser = new User(name);
        if (userService.insertUser(newUser)) {
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<NullValue> deleteUser() {
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
