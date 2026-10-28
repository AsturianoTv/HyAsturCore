package tv.asturiano.revive;

import java.util.UUID;

public class ReviveListener {

    private final ReviveManager reviveManager;

    public ReviveListener(ReviveManager reviveManager) {
        this.reviveManager = reviveManager;
    }

    public void onPlayerDamage(UUID playerUUID, float remainingHealth, int x, int y, int z) {
        if (remainingHealth <= 0.0f && !reviveManager.isPlayerDowned(playerUUID)) {
            reviveManager.setPlayerDowned(playerUUID, x, y, z);
        }
    }

    public void onPlayerInteract(UUID healerUUID, UUID targetUUID) {
        if (reviveManager.isPlayerDowned(targetUUID)) {
            reviveManager.revivePlayer(targetUUID);
            System.out.println("[Revive] El jugador " + healerUUID + " auxilió y revivió a " + targetUUID);
        }
    }
}
