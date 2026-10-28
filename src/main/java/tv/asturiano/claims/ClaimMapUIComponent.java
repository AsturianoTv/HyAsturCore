package tv.asturiano.claims;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class ClaimMapUIComponent {
    private final String uiNamespace = "hyastur:HyAsturClaimMapUi";
    private final int centerChunkX;
    private final int centerChunkZ;
    private final Map<String, Object> gridData = new HashMap<>();

    public ClaimMapUIComponent(ClaimManager claimManager, String world, UUID playerUuid, int playerChunkX, int playerChunkZ) {
        this.centerChunkX = playerChunkX;
        this.centerChunkZ = playerChunkZ;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int cX = playerChunkX + dx;
                int cZ = playerChunkZ + dz;
                UUID owner = claimManager.getOwner(world, cX, cZ);

                String btnTextKey = "Btn_" + dx + "_" + dz;
                String textVal;

                if (dx == 0 && dz == 0) {
                    textVal = "§a[P]" + cX + "," + cZ;
                } else if (owner == null) {
                    textVal = "[" + cX + "," + cZ + "]\nLibre";
                } else if (owner.equals(playerUuid)) {
                    textVal = "[" + cX + "," + cZ + "]\nTuyo";
                } else {
                    textVal = "[" + cX + "," + cZ + "]\nAgeno";
                }

                gridData.put(btnTextKey, textVal);
            }
        }
    }

    public String getUiNamespace() {
        return uiNamespace;
    }

    public Map<String, Object> getGridData() {
        return gridData;
    }

    public int getCenterChunkX() {
        return centerChunkX;
    }

    public int getCenterChunkZ() {
        return centerChunkZ;
    }
}
