package inqool.project.tennisreservationsystem;

import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import inqool.project.tennisreservationsystem.service.CourtTypeService;
import inqool.project.tennisreservationsystem.service.TennisCourtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class TennisCourtTests {

    @Autowired
    private CourtTypeService courtTypeService;
    @Autowired
    private TennisCourtService tennisCourtService;

    @Test
    public void CreationOfCourtType() {
        CourtType test = new CourtType("Test", 1337);
        CourtType response = courtTypeService.insertEntity(test);

        assert response.equals(test);
    }

    @Test
    public void CreationOfTennisCourt() {
        CourtType courtTypeTest = new CourtType("Test", 1337);
        CourtType courtTypeResponse = courtTypeService.insertEntity(courtTypeTest);

        TennisCourt tennisCourtTest = new TennisCourt(courtTypeResponse);
        TennisCourt tennisCourtResponse = tennisCourtService.insertEntity(tennisCourtTest);

        assert tennisCourtResponse != null;
        assert tennisCourtResponse.getCourtType().equals(courtTypeResponse);
    }

    @Test
    public void RetrievalOfCourtType() {
        CourtType courtTypeTest = new CourtType("Test", 1337);
        CourtType courtTypeResponse = courtTypeService.insertEntity(courtTypeTest);

        CourtType courtTypeRetrieval = courtTypeService.getEntity(courtTypeResponse.getId());

        assert courtTypeRetrieval.equals(courtTypeResponse);
    }

    @Test
    public void RetrievalOfTennisCourt() {
        CourtType courtTypeTest = new CourtType("Test", 1337);
        CourtType courtTypeResponse = courtTypeService.insertEntity(courtTypeTest);

        TennisCourt tennisCourtTest = new TennisCourt(courtTypeResponse);
        TennisCourt tennisCourtResponse = tennisCourtService.insertEntity(tennisCourtTest);

        TennisCourt tennisCourtRetrieval = tennisCourtService.getEntity(tennisCourtResponse.getId());

        assert tennisCourtRetrieval != null;
        assert tennisCourtRetrieval.getCourtType().equals(courtTypeResponse);
    }

    @Test
    public void DeletionOfTennisCourt() {
        CourtType courtTypeResponse = courtTypeService.insertEntity(new CourtType("Test", 1337));

        TennisCourt tennisCourtTest = new TennisCourt(courtTypeResponse);
        TennisCourt tennisCourtResponse = tennisCourtService.insertEntity(tennisCourtTest);

        tennisCourtService.softEntityDelete(tennisCourtResponse.getId());

        assert !tennisCourtService.hasEntity(tennisCourtResponse.getId());
    }

    @Test
    public void UpdatingOfTennisCourt() {
        CourtType courtType = courtTypeService.insertEntity(new CourtType("Test", 1337));
        CourtType courtType1 = courtTypeService.insertEntity(new CourtType("Another One", 15));
        TennisCourt tennisCourtResponse = tennisCourtService.insertEntity(new TennisCourt(courtType));

        assert tennisCourtResponse != null;

        tennisCourtResponse.setCourtType(courtType1);
        TennisCourt tennisCourtResponse1 = tennisCourtService.updateEntity(tennisCourtResponse);

        assert tennisCourtResponse1.getCourtType().equals(courtType1);
    }
}
