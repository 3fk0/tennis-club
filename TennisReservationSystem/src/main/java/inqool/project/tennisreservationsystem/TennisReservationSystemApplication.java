package inqool.project.tennisreservationsystem;

import inqool.project.tennisreservationsystem.data.TestData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Tennis Court Reservation System
 * The application can be booted up with the --init argument which will
 * create 2 CourtTypes and 4 TennisCourts
 *
 * @author Boris Lukačovič
 */

@SpringBootApplication
public class TennisReservationSystemApplication {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--init")) {
            TestData.initData();
        }

        SpringApplication.run(TennisReservationSystemApplication.class, args);

    }

}
