package tv.asturiano.breeding;

import java.util.UUID;

public class BreedingData {

    private final UUID entityUuid;
    private long lastBredTimestamp;
    private final long cooldownMs;

    public BreedingData(UUID entityUuid, long cooldownMs) {
        this.entityUuid = entityUuid;
        this.cooldownMs = cooldownMs;
        this.lastBredTimestamp = 0;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public boolean canBreed() {
        return (System.currentTimeMillis() - lastBredTimestamp) >= cooldownMs;
    }

    public void setBredNow() {
        this.lastBredTimestamp = System.currentTimeMillis();
    }

    public long getRemainingCooldownSeconds() {
        long elapsed = System.currentTimeMillis() - lastBredTimestamp;
        long remaining = cooldownMs - elapsed;
        return remaining > 0 ? remaining / 1000 : 0;
    }
}
