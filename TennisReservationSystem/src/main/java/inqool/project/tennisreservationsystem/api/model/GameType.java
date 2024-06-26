package inqool.project.tennisreservationsystem.api.model;

import java.time.Duration;

/**
 * Enum for the possible game that could be played.
 * Each gameType has its price multiplier.
 *
 * @author Boris Lukačovič
 */
public enum GameType {
    SINGLES(1f), DOUBLES(1.5f);

    private final float priceMultiplier;

    GameType(float priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public float getFinalPrice(float rentForHour, Duration duration) {
        return duration.toMinutes() * rentForHour / 60 * priceMultiplier;
    }
}
