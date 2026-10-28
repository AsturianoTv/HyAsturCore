package tv.asturiano.claims;

import java.util.UUID;

public class ClaimUIListener {
    private final ClaimManager claimManager;

    public ClaimUIListener(ClaimManager claimManager) {
        this.claimManager = claimManager;
        System.out.println("[HyAstur-Debug] ClaimUIListener enlazado y listo para recibir eventos de UI.");
    }

    public void handleAction(UUID playerUuid, String actionId, int playerChunkX, int playerChunkZ, String world) {
        if (actionId == null) return;

        System.out.println("[HyAstur-Debug] Clic de UI recibido de " + playerUuid + " -> Acción: " + actionId);

        if (actionId.equals("CLOSE_MAP")) {
            return;
        }

        if (actionId.startsWith("CHUNK_")) {
            try {
                String[] parts = actionId.replace("CHUNK_", "").split("_");
                if (parts.length != 2) return;

                int dx = Integer.parseInt(parts[0]);
                int dz = Integer.parseInt(parts[1]);
                
                int targetX = playerChunkX + dx;
                int targetZ = playerChunkZ + dz;

                UUID owner = claimManager.getOwner(world, targetX, targetZ);
                if (owner == null) {
                    claimManager.claim(world, targetX, targetZ, playerUuid);
                    System.out.println("[HyAstur-Debug] Chunk [" + targetX + ", " + targetZ + "] reclamado correctamente.");
                } else if (owner.equals(playerUuid)) {
                    claimManager.unclaim(world, targetX, targetZ);
                    System.out.println("[HyAstur-Debug] Chunk [" + targetX + ", " + targetZ + "] liberado correctamente.");
                }
            } catch (Exception e) {
                System.err.println("[HyAstur-Error] Error procesando clic de UI: " + e.getMessage());
            }
        }
    }
}
