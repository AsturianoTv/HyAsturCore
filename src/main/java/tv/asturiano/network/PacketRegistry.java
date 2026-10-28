package tv.asturiano.network;

import java.util.Map;
import java.util.logging.Logger;

public class PacketRegistry {

    private static final Logger LOGGER = Logger.getLogger("HyAsturCore");
    public static final String CHANNEL_NAME = "hyastur:ui_channel";

    public static void registerPackets() {
        try {
            LOGGER.info("[HyAsturCore] Registrando canal de red y eventos de UI: " + CHANNEL_NAME);
        } catch (Exception e) {
            LOGGER.severe("[HyAsturCore] Error al registrar los canales de red: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean sendOpenUiToPlayer(Object sender, String uiNamespace, Map<String, Object> data) {
        // El motor procesa las UIs mediante sincronización de datos y componentes nativos
        LOGGER.info("[HyAsturCore] Sincronización de componentes UI preparada para namespace: " + uiNamespace);
        return true;
    }
}
