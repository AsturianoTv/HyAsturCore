package tv.asturiano.claims;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;

public class ClaimMenuUI {
    private final ClaimManager claimManager;
    private final ClaimUIListener uiListener;

    public ClaimMenuUI(ClaimManager claimManager) {
        this.claimManager = claimManager;
        this.uiListener = new ClaimUIListener(claimManager);
    }

    public void openGridMap(Store<EntityStore> store, Ref<EntityStore> ref, World world, UUID playerUuid, int playerChunkX, int playerChunkZ) {
        if (store == null || ref == null || world == null || playerUuid == null) return;

        try {
            HyAsturClaimMapScreen screen = new HyAsturClaimMapScreen(claimManager, playerUuid, world.getName(), playerChunkX, playerChunkZ);
            screen.openForPlayer(store, ref);
            System.out.println("[HyAstur-Debug] openGridMap ejecutado correctamente para el jugador " + playerUuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClaimUIListener getUiListener() {
        return uiListener;
    }
}
