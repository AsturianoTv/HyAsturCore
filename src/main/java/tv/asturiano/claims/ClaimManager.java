package tv.asturiano.claims;

import tv.asturiano.database.DatabaseManager;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimManager {
    private final DatabaseManager databaseManager;
    private final ConcurrentHashMap<String, UUID> claimCache = new ConcurrentHashMap<>();

    public ClaimManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        System.out.println("[HyAstur-Debug] ClaimManager inicializado correctamente.");
    }

    private String getCacheKey(String world, int x, int z) {
        return world + "_" + x + "_" + z;
    }

    public boolean isClaimed(String world, int x, int z) {
        String key = getCacheKey(world, x, z);
        if (claimCache.containsKey(key)) {
            System.out.println("[HyAstur-Debug] isClaimed: Hit en caché para chunk [" + x + ", " + z + "] en mundo " + world);
            return true;
        }

        String owner = databaseManager.getClaimOwner(world, x, z);
        if (owner != null) {
            claimCache.put(key, UUID.fromString(owner));
            System.out.println("[HyAstur-Debug] isClaimed: Encontrado en BD para chunk [" + x + ", " + z + "] -> Dueño: " + owner);
            return true;
        }
        return false;
    }

    public UUID getOwner(String world, int x, int z) {
        String key = getCacheKey(world, x, z);
        if (claimCache.containsKey(key)) {
            return claimCache.get(key);
        }

        String ownerStr = databaseManager.getClaimOwner(world, x, z);
        if (ownerStr != null) {
            try {
                UUID uuid = UUID.fromString(ownerStr);
                claimCache.put(key, uuid);
                return uuid;
            } catch (IllegalArgumentException e) {
                System.err.println("[HyAstur-Error] El UUID almacenado en la BD no es válido para el chunk [" + x + ", " + z + "]: " + ownerStr);
            }
        }
        return null;
    }

    public boolean claim(String world, int x, int z, UUID playerUuid) {
        System.out.println("[HyAstur-Debug] Intentando reclamar chunk [" + x + ", " + z + "] en " + world + " para el jugador " + playerUuid);
        boolean success = databaseManager.claimChunk(world, x, z, playerUuid);
        if (success) {
            claimCache.put(getCacheKey(world, x, z), playerUuid);
            System.out.println("[HyAstur-Debug] Chunk [" + x + ", " + z + "] reclamado con éxito.");
        } else {
            System.err.println("[HyAstur-Error] Falló la base de datos al intentar reclamar el chunk [" + x + ", " + z + "]");
        }
        return success;
    }

    public boolean unclaim(String world, int x, int z) {
        System.out.println("[HyAstur-Debug] Intentando desreclamar chunk [" + x + ", " + z + "] en " + world);
        boolean success = databaseManager.unclaimChunk(world, x, z);
        if (success) {
            claimCache.remove(getCacheKey(world, x, z));
            System.out.println("[HyAstur-Debug] Chunk [" + x + ", " + z + "] desreclamado con éxito.");
        } else {
            System.err.println("[HyAstur-Error] Falló la base de datos al intentar desreclamar el chunk [" + x + ", " + z + "]");
        }
        return success;
    }

    public int getPlayerClaimCount(String world, UUID playerUuid) {
        return databaseManager.getClaimCount(world, playerUuid);
    }

    public boolean canClaimMore(String world, UUID playerUuid, int maxAllowed) {
        int current = getPlayerClaimCount(world, playerUuid);
        System.out.println("[HyAstur-Debug] Jugador " + playerUuid + " tiene " + current + " claims de un máximo de " + maxAllowed);
        return current < maxAllowed;
    }
}
