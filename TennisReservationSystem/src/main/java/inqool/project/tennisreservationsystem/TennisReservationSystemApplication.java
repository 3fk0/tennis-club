package inqool.project.tennisreservationsystem;

import inqool.project.tennisreservationsystem.data.TestData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TennisReservationSystemApplication {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--init")) {
            TestData.initData();
        }

        SpringApplication.run(TennisReservationSystemApplication.class, args);

    }

}
