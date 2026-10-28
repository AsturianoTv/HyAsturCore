package tv.asturiano.admin;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.Message;
import tv.asturiano.HyAsturMod;
import java.util.concurrent.CompletableFuture;

public class HyAsturReloadCommand extends AbstractCommand {

    public HyAsturReloadCommand() {
        super("hyreload", "Recarga la configuración de HyAsturCore.");
    }

    @Override
    public CompletableFuture<Void> execute(CommandContext context) {
        try {
            HyAsturMod.getInstance().getConfigManager().loadConfig();
            context.sendMessage(Message.raw("§a[HyAstur] ¡Configuración recargada con éxito desde el disco!"));
        } catch (Exception e) {
            context.sendMessage(Message.raw("§c[HyAstur] Error al recargar la configuración: " + e.getMessage()));
        }
        return CompletableFuture.completedFuture(null);
    }
}
