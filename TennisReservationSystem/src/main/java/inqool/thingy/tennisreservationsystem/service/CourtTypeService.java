package inqool.thingy.tennisreservationsystem.service;

import inqool.thingy.tennisreservationsystem.api.model.CourtType;
import org.springframework.stereotype.Service;

@Service
public class CourtTypeService extends EntityService<CourtType, Long> {

    public CourtTypeService() {
        super(CourtType.class);
    }
}
