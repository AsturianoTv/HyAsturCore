package tv.asturiano.admin;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.Message;
import tv.asturiano.claims.ClaimManager;

import java.util.concurrent.CompletableFuture;

public class AdminUnclaimCommand extends AbstractCommand {

    private final ClaimManager claimManager;

    public AdminUnclaimCommand(ClaimManager claimManager) {
        super("adminunclaim", "Fuerza la liberación del terreno actual (Modo Admin).");
        this.claimManager = claimManager;
        System.out.println("[HyAstur-Debug] AdminUnclaimCommand registrado correctamente.");
    }

    @Override
    public CompletableFuture<Void> execute(CommandContext context) {
        try {
            Object sender = context.sender();
            if (sender == null) {
                System.out.println("[HyAstur-Warning] AdminUnclaimCommand ejecutado sin un sender válido.");
                return CompletableFuture.completedFuture(null);
            }

            int chunkX = 0;
            int chunkZ = 0;
            String worldName = "asturias";

            // Extracción segura de coordenadas mediante reflexión (independiente de ClaimCommand)
            try {
                Object posObj = null;
                for (java.lang.reflect.Method m : sender.getClass().getMethods()) {
                    String name = m.getName().toLowerCase();
                    if (m.getParameterCount() == 0 && (name.contains("position") || name.contains("transform") || name.contains("location"))) {
                        m.setAccessible(true);
                        posObj = m.invoke(sender);
                        if (posObj != null) break;
                    }
                }

                if (posObj != null) {
                    double px = 0, pz = 0;
                    for (java.lang.reflect.Method m : posObj.getClass().getMethods()) {
                        if (m.getParameterCount() == 0) {
                            if (m.getName().equalsIgnoreCase("getX")) px = (double) m.invoke(posObj);
                            if (m.getName().equalsIgnoreCase("getZ")) pz = (double) m.invoke(posObj);
                        }
                    }
                    chunkX = ((int) px) >> 4;
                    chunkZ = ((int) pz) >> 4;
                    System.out.println("[HyAstur-Debug] AdminUnclaim calculado -> Chunk [" + chunkX + ", " + chunkZ + "] (Coord: " + px + ", " + pz + ")");
                } else {
                    System.out.println("[HyAstur-Warning] No se pudo obtener la posición en AdminUnclaimCommand, usando [0, 0].");
                }
            } catch (Exception e) {
                System.err.println("[HyAstur-Error] Fallo al extraer coordenadas por reflexión en AdminUnclaimCommand: " + e.getMessage());
            }

            if (claimManager == null) {
                System.err.println("[HyAstur-Error] AdminUnclaimCommand: claimManager es nulo.");
                context.sendMessage(Message.raw("§c[HyAstur] Error: Gestor de claims no disponible."));
                return CompletableFuture.completedFuture(null);
            }

            System.out.println("[HyAstur-Debug] Verificando propiedad para forzar liberación en chunk [" + chunkX + ", " + chunkZ + "]...");
            if (claimManager.getOwner(worldName, chunkX, chunkZ) == null) {
                System.out.println("[HyAstur-Debug] El chunk [" + chunkX + ", " + chunkZ + "] no está reclamado.");
                context.sendMessage(Message.raw("§e[HyAstur-Admin] El chunk (" + chunkX + ", " + chunkZ + ") no está reclamado."));
                return CompletableFuture.completedFuture(null);
            }

            boolean success = claimManager.unclaim(worldName, chunkX, chunkZ);
            if (success) {
                System.out.println("[HyAstur-Debug] Terreno liberado forzosamente con éxito en chunk [" + chunkX + ", " + chunkZ + "]");
                context.sendMessage(Message.raw("§a[HyAstur-Admin] ¡Terreno liberado forzosamente en el chunk (" + chunkX + ", " + chunkZ + ")!"));
            } else {
                System.err.println("[HyAstur-Error] Falló la base de datos al intentar el unclaim forzoso en [" + chunkX + ", " + chunkZ + "]");
                context.sendMessage(Message.raw("§c[HyAstur-Admin] Error al intentar eliminar el registro de la base de datos."));
            }

        } catch (Exception e) {
            System.err.println("[HyAstur-Error] Error crítico en AdminUnclaimCommand: " + e.getMessage());
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }
}
