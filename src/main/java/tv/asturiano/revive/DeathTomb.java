package tv.asturiano.revive;

import java.util.UUID;

public class DeathTomb {

    private final UUID ownerUUID;
    private final int x, y, z;
    private final long expireTime;

    public DeathTomb(UUID ownerUUID, int x, int y, int z, long durationMs) {
        this.ownerUUID = ownerUUID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.expireTime = System.currentTimeMillis() + durationMs;
    }

    public UUID getOwnerUUID() { return ownerUUID; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}
