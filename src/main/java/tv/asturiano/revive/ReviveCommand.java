package tv.asturiano.revive;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReviveCommand extends AbstractCommand {

    private final ReviveManager reviveManager;

    public ReviveCommand(ReviveManager reviveManager) {
        super("revive", "Reanima a un jugador o ríndete si estás abatido");
        this.reviveManager = reviveManager;
    }

    @Override
    public CompletableFuture<Void> execute(CommandContext context) {
        UUID playerUUID = UUID.randomUUID();
        if (context.isPlayer() && context.sender() != null) {
            try {
                playerUUID = context.sender().getUuid();
            } catch (Exception ignored) {}
        }

        String input = context.getInputString();
        if (input != null && input.toLowerCase().contains("giveup")) {
            if (reviveManager.isPlayerDowned(playerUUID)) {
                reviveManager.killPlayerCompletely(playerUUID);
                context.sendMessage(Message.raw("Te has rendido. Reapareciendo..."));
            } else {
                context.sendMessage(Message.raw("No estás en estado abatido."));
            }
            return CompletableFuture.completedFuture(null);
        }

        if (reviveManager.isPlayerDowned(playerUUID)) {
            reviveManager.revivePlayer(playerUUID);
            context.sendMessage(Message.raw("¡Te has reanimado a ti mismo o has sido auxiliado!"));
        } else {
            context.sendMessage(Message.raw("Usa '/revive giveup' para rendirte y reaparecer si estás en el suelo."));
        }
        return CompletableFuture.completedFuture(null);
    }
}
