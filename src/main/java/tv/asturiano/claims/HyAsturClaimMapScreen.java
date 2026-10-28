package tv.asturiano.claims;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.entityui.UIComponentList;

public class HyAsturClaimMapScreen {
    private final ClaimManager claimManager;
    private final UUID playerUuid;
    private final String worldName;
    private final int centerChunkX;
    private final int centerChunkZ;

    public HyAsturClaimMapScreen(ClaimManager claimManager, UUID playerUuid, String world, int centerChunkX, int centerChunkZ) {
        this.claimManager = claimManager;
        this.playerUuid = playerUuid;
        this.worldName = world;
        this.centerChunkX = centerChunkX;
        this.centerChunkZ = centerChunkZ;
    }

    public String getUINamespace() {
        return "tv.asturiano:HyAsturClaimMapUi";
    }

    public Map<String, Object> getData() {
        Map<String, Object> uiData = new HashMap<>();
        uiData.put("playerChunkX", centerChunkX);
        uiData.put("playerChunkZ", centerChunkZ);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int cX = centerChunkX + dx;
                int cZ = centerChunkZ + dz;
                UUID owner = claimManager.getOwner(worldName, cX, cZ);

                String btnTextKey = "Btn_" + dx + "_" + dz;
                String textVal;

                if (dx == 0 && dz == 0) {
                    textVal = "[P] " + cX + "," + cZ;
                } else if (owner == null) {
                    textVal = "[" + cX + "," + cZ + "]\nLibre";
                } else if (owner.equals(playerUuid)) {
                    textVal = "[" + cX + "," + cZ + "]\nTuyo";
                } else {
                    textVal = "[" + cX + "," + cZ + "]\nAgeno";
                }

                uiData.put(btnTextKey, textVal);
            }
        }
        return uiData;
    }

    public boolean openForPlayer(Store<EntityStore> store, Ref<EntityStore> ref) {
        try {
            if (store == null || ref == null) return false;

            String uiNamespace = getUINamespace();
            Map<String, Object> data = getData();

            System.out.println("[HyAsturCore-Debug] Despachando pantalla UI '" + uiNamespace + "' vía ECS Store.");

            UIComponentList uiComponent = store.getComponent(ref, UIComponentList.getComponentType());
            if (uiComponent == null) {
                System.err.println("[HyAstur-Error] UIComponentList no encontrado en la entidad.");
                return false;
            }

            for (Method m : uiComponent.getClass().getMethods()) {
                String name = m.getName().toLowerCase();
                if ((name.contains("open") || name.contains("show") || name.contains("push") || name.contains("screen"))
                        && m.getParameterCount() >= 1) {
                    try {
                        m.setAccessible(true);
                        Class<?>[] pTypes = m.getParameterTypes();
                        if (pTypes.length == 1 && pTypes[0] == String.class) {
                            m.invoke(uiComponent, uiNamespace);
                            break;
                        } else if (pTypes.length == 2 && pTypes[0] == String.class && Map.class.isAssignableFrom(pTypes[1])) {
                            m.invoke(uiComponent, uiNamespace, data);
                            break;
                        }
                    } catch (Exception ignored) {}
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
