package tv.asturiano.claims;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import tv.asturiano.database.DatabaseManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class ClaimsListCommand extends AbstractPlayerCommand {
    private final DatabaseManager databaseManager;

    public ClaimsListCommand(DatabaseManager databaseManager) {
        super("claims", "Muestra una lista de todos tus terrenos reclamados.");
        this.databaseManager = databaseManager;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID playerUuid = playerRef.getUuid();
        if (playerUuid == null) {
            context.sendMessage(Message.raw("§cEste comando solo puede ser ejecutado por un jugador."));
            return;
        }

        String worldName = world.getName() != null ? world.getName() : "asturias";

        context.sendMessage(Message.raw("§6[HyAstur] Consultando tus terrenos registrados..."));

        try {
            List<String> claims = databaseManager.getPlayerClaimsList(worldName, playerUuid);
            
            if (claims == null || claims.isEmpty()) {
                context.sendMessage(Message.raw("§e[HyAstur] Actualmente no tienes ningún terreno reclamado en este mundo."));
                return;
            }

            context.sendMessage(Message.raw("§8[§aHyAstur§8] §aTus terrenos reclamados (" + claims.size() + "):"));
            for (String claimCoord : claims) {
                context.sendMessage(Message.raw(" §7- Chunk: §e" + claimCoord));
            }

        } catch (Exception e) {
            context.sendMessage(Message.raw("§c[HyAstur] Error al consultar la base de datos de claims."));
            e.printStackTrace();
        }
    }
}
