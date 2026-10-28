package tv.asturiano.claims;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import tv.asturiano.HyAsturMod;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ClaimCommand extends AbstractPlayerCommand {

    private final ClaimManager claimManager;
    private final ClaimMenuUI claimMenuUI;

    public ClaimCommand(ClaimManager claimManager) {
        super("claim", "Reclama el terreno actual y abre la interfaz de mapa.");
        this.claimManager = claimManager;
        this.claimMenuUI = new ClaimMenuUI(claimManager);
        System.out.println("[HyAstur-Debug] ClaimCommand registrado correctamente.");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID playerUuid = playerRef.getUuid();
        if (playerUuid == null) {
            System.out.println("[HyAstur-Warning] ClaimCommand ejecutado sin un UUID de jugador válido.");
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
        } catch (Exception e) {
            System.err.println("[HyAstur-Error] Fallo al obtener la posición del jugador en ClaimCommand: " + e.getMessage());
        }

        int chunkX = (int) Math.floor(posX / 16.0);
        int chunkZ = (int) Math.floor(posZ / 16.0);
        String worldName = world.getName() != null ? world.getName() : "asturias";

        int maxClaimsAllowed = 10;
        try {
            maxClaimsAllowed = HyAsturMod.getInstance().getConfigManager().getMaxClaimsPerPlayer();
        } catch (Exception ignored) {}

        System.out.println("[HyAstur-Debug] Jugador " + playerUuid + " intenta reclamar en chunk [" + chunkX + ", " + chunkZ + "] en mundo '" + worldName + "'");

        if (!claimManager.canClaimMore(worldName, playerUuid, maxClaimsAllowed)) {
            System.out.println("[HyAstur-Debug] El jugador ha alcanzado el límite de claims.");
            context.sendMessage(Message.raw("§c[HyAstur] Has alcanzado el límite máximo de terrenos reclamados (" + maxClaimsAllowed + ")."));
            return;
        }

        boolean success = claimManager.claim(worldName, chunkX, chunkZ, playerUuid);

        if (success) {
            int current = claimManager.getPlayerClaimCount(worldName, playerUuid);
            System.out.println("[HyAstur-Debug] Claim realizado con éxito. Total actual: " + current);
            context.sendMessage(Message.raw("§8[§aHyAstur§8] §a¡Has reclamado el chunk §e(" + chunkX + ", " + chunkZ + ")§a! (" + current + "/" + maxClaimsAllowed + ")"));
        } else {
            System.out.println("[HyAstur-Debug] El chunk [" + chunkX + ", " + chunkZ + "] ya estaba ocupado.");
            context.sendMessage(Message.raw("§8[§eHyAstur§8] §eEste chunk (" + chunkX + ", " + chunkZ + ") ya pertenece a otro jugador."));
        }

        // Abrir la UI gráfica del mapa pasando la infraestructura ECS correcta (store, ref, world)
        System.out.println("[HyAstur-Debug] Invocando claimMenuUI.openGridMap desde ClaimCommand...");
        claimMenuUI.openGridMap(store, ref, world, playerUuid, chunkX, chunkZ);
    }

    public static UUID resolvePlayerUuid(Object sender) {
        if (sender == null) return null;
        try {
            java.lang.reflect.Method m = sender.getClass().getMethod("getUuid");
            return (UUID) m.invoke(sender);
        } catch (Exception e1) {
            try {
                java.lang.reflect.Method m = sender.getClass().getMethod("getUniqueId");
                return (UUID) m.invoke(sender);
            } catch (Exception e2) {
                try {
                    java.lang.reflect.Field f = sender.getClass().getDeclaredField("uuid");
                    f.setAccessible(true);
                    return (UUID) f.get(sender);
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
