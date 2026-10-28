package tv.asturiano.breeding;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BreedingManager {

    private final Map<UUID, BreedingData> breedingRegistry = new ConcurrentHashMap<>();
    private final long defaultCooldownMs;

    public BreedingManager() {
        // Cooldown por defecto: 5 minutos (300,000 ms)
        this.defaultCooldownMs = 300_000L;
    }

    public boolean canBreed(UUID entityUuid) {
        BreedingData data = breedingRegistry.computeIfAbsent(entityUuid, 
            id -> new BreedingData(id, defaultCooldownMs));
        return data.canBreed();
    }

    public void markAsBred(UUID entityUuid) {
        BreedingData data = breedingRegistry.computeIfAbsent(entityUuid, 
            id -> new BreedingData(id, defaultCooldownMs));
        data.setBredNow();
    }

    public long getCooldownRemaining(UUID entityUuid) {
        BreedingData data = breedingRegistry.get(entityUuid);
        return data != null ? data.getRemainingCooldownSeconds() : 0;
    }
}
