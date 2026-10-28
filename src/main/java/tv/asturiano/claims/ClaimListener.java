package tv.asturiano.claims;

import java.util.UUID;

public class ClaimListener {

    private final ClaimManager claimManager;

    public ClaimListener(ClaimManager claimManager) {
        this.claimManager = claimManager;
    }

    public void handleBreakBlock(Object event) {
        processBlockProtection(event, "break");
    }

    public void handlePlaceBlock(Object event) {
        processBlockProtection(event, "place");
    }

    public void handleUseBlock(Object event) {
        processBlockProtection(event, "use");
    }

    private void processBlockProtection(Object event, String actionType) {
        if (event == null || claimManager == null) return;

        try {
            // Si el terreno no está reclamado por ningún jugador, PERMITIR SIEMPRE
            // Esto permite alimentar animales, usar mesas de trabajo y romper bloques libres
            java.lang.reflect.Method getPlayer = event.getClass().getMethod("getPlayer");
            Object player = getPlayer.invoke(event);
            if (player == null) return;

            UUID playerUuid = ClaimCommand.resolvePlayerUuid(player);
            if (playerUuid == null) return;

            // Obtener coordenadas del chunk desde el evento
            java.lang.reflect.Method getTargetBlock = event.getClass().getMethod("getTargetBlock");
            Object block = getTargetBlock.invoke(event);
            if (block == null) return;

            java.lang.reflect.Method getX = block.getClass().getMethod("getX");
            java.lang.reflect.Method getZ = block.getClass().getMethod("getZ");
            int blockX = (int) getX.invoke(block);
            int blockZ = (int) getZ.invoke(block);

            int chunkX = blockX >> 4;
            int chunkZ = blockZ >> 4;
            String worldName = "asturias";

            UUID owner = claimManager.getOwner(worldName, chunkX, chunkZ);

            // Si el chunk tiene dueño y NO es el jugador actual, cancelar la acción
            if (owner != null && !owner.equals(playerUuid)) {
                java.lang.reflect.Method setCancelled = event.getClass().getMethod("setCancelled", boolean.class);
                setCancelled.invoke(event, true);
            }
        } catch (Exception ignored) {
            // En caso de duda o si no es un bloque protegido, no cancelar nada
        }
    }
}
