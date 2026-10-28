package tv.asturiano.claims;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import java.util.UUID;

public class ClaimMapCommand extends AbstractPlayerCommand {
    private final ClaimManager claimManager;
    private final ClaimMenuUI claimMenuUI;

    public ClaimMapCommand(ClaimManager claimManager) {
        super("claimmap", "Abre el mapa interactivo de reclamación de terrenos");
        this.claimManager = claimManager;
        this.claimMenuUI = new ClaimMenuUI(claimManager);
        System.out.println("[HyAstur-Debug] ClaimMapCommand registrado correctamente.");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID playerUuid = playerRef.getUuid();
        if (playerUuid == null) return;

        double posX = 0.0;
        double posZ = 0.0;
        try {
            var transform = store.getComponent(ref, com.hypixel.hytale.server.core.modules.entity.component.TransformComponent.getComponentType());
            if (transform != null && transform.getPosition() != null) {
                posX = transform.getPosition().x();
                posZ = transform.getPosition().z();
            }
        } catch (Exception ignored) {}

        int chunkX = (int) Math.floor(posX / 16.0);
        int chunkZ = (int) Math.floor(posZ / 16.0);
        String worldName = world.getName() != null ? world.getName() : "asturias";

        System.out.println("[HyAstur-Debug] Abriendo mapa gráfico mediante ClaimMapCommand para " + playerUuid + " en [" + chunkX + ", " + chunkZ + "]");
        
        claimMenuUI.openGridMap(store, ref, world, playerUuid, chunkX, chunkZ);
    }
}
