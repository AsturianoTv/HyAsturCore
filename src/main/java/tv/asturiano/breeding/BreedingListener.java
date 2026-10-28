package tv.asturiano.breeding;

import java.lang.reflect.Method;
import java.util.UUID;

public class BreedingListener {

    private final BreedingManager breedingManager;

    public BreedingListener(BreedingManager breedingManager) {
        this.breedingManager = breedingManager;
    }

    public void handleEntityInteract(Object event) {
        if (event == null) return;

        try {
            UUID entityUuid = getUuidProperty(event, "getEntityUuid", "getTargetEntityUuid", "getEntity");
            UUID playerUuid = getUuidProperty(event, "getPlayerUuid", "getPlayer");

            if (entityUuid != null && playerUuid != null) {
                if (!breedingManager.canBreed(entityUuid)) {
                    long remaining = breedingManager.getCooldownRemaining(entityUuid);
                    System.out.println("[Breeding] Esta criatura debe esperar " + remaining + "s para reproducirse.");
                    return;
                }

                // Lógica de apareamiento iniciada
                breedingManager.markAsBred(entityUuid);
                System.out.println("[Breeding] Apareamiento exitoso registrado para la entidad " + entityUuid);
            }
        } catch (Exception ignored) {}
    }

    private UUID getUuidProperty(Object obj, String... candidateMethods) {
        for (String methodName : candidateMethods) {
            try {
                Method m = obj.getClass().getMethod(methodName);
                Object res = m.invoke(obj);
                if (res instanceof UUID) return (UUID) res;
            } catch (Exception ignored) {}
        }
        return null;
    }
}
