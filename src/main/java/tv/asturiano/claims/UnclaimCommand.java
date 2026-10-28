package tv.asturiano.claims;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.UUID;

public class UnclaimCommand extends AbstractPlayerCommand {
    private final ClaimManager claimManager;

    public UnclaimCommand(ClaimManager claimManager) {
        super("unclaim", "Libera el terreno actual en el que te encuentras.");
        this.claimManager = claimManager;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID playerUuid = playerRef.getUuid();
        if (playerUuid == null) {
            context.sendMessage(Message.raw("§cEste comando solo puede ser ejecutado por un jugador."));
            return;
        }

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

        UUID owner = claimManager.getOwner(worldName, chunkX, chunkZ);

        if (owner == null) {
            context.sendMessage(Message.raw("§e[HyAstur] Este chunk (" + chunkX + ", " + chunkZ + ") no está reclamado."));
        } else if (owner.equals(playerUuid)) {
            claimManager.unclaim(worldName, chunkX, chunkZ);
            context.sendMessage(Message.raw("§a[HyAstur] Has liberado correctamente el chunk (" + chunkX + ", " + chunkZ + ")."));
        } else {
            context.sendMessage(Message.raw("§c[HyAstur] No puedes liberar este terreno porque pertenece a otro jugador."));
        }
    }
}
