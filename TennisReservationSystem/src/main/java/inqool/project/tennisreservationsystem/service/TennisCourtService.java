package inqool.project.tennisreservationsystem.service;

import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import org.springframework.stereotype.Service;

/**
 * Service class for TennisCourt.
 *
 * @author Boris Lukačovič
 */
@Service
public class TennisCourtService extends EntityService<TennisCourt, Long> {
    public TennisCourtService() {
        super(TennisCourt.class);
    }
}
