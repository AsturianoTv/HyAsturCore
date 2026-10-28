package tv.asturiano.revive;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ReviveManager {

    private final Map<UUID, Integer> downedPlayers = new ConcurrentHashMap<>();

    public void downPlayer(UUID playerUUID) {
        downedPlayers.put(playerUUID, 30);
        System.out.println("[Revive] Jugador " + playerUUID + " está abatido. Ventana de sangrado abierta.");
    }

    public void setPlayerDowned(UUID playerUUID, int x, int y, int z) {
        downedPlayers.put(playerUUID, 30);
        System.out.println("[Revive] Jugador " + playerUUID + " abatido en X:" + x + " Y:" + y + " Z:" + z);
    }

    public boolean isPlayerDowned(UUID playerUUID) {
        return downedPlayers.containsKey(playerUUID);
    }

    public void revivePlayer(UUID playerUUID) {
        downedPlayers.remove(playerUUID);
        System.out.println("[Revive] Jugador " + playerUUID + " ha sido reanimado.");
    }

    public void killPlayerCompletely(UUID playerUUID) {
        downedPlayers.remove(playerUUID);
        System.out.println("[Revive] Jugador " + playerUUID + " se rindió o expiró el tiempo y murió.");
    }

    public void tick() {
        for (Map.Entry<UUID, Integer> entry : downedPlayers.entrySet()) {
            UUID uuid = entry.getKey();
            int timeLeft = entry.getValue() - 1;
            if (timeLeft <= 0) {
                killPlayerCompletely(uuid);
            } else {
                downedPlayers.put(uuid, timeLeft);
            }
        }
    }
}
