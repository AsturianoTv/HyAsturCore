package tv.asturiano.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class DatabaseManager {
    private Connection connection;
    private final String dbPath;

    public DatabaseManager(String dataPath) {
        this.dbPath = "jdbc:sqlite:" + dataPath + "/hyastur_claims.db";
    }

    public void connect() {
        try {
            // Forzar la carga manual del driver SQLite para entornos aislados
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(dbPath);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS claims (" +
                        "world TEXT NOT NULL, " +
                        "chunk_x INT NOT NULL, " +
                        "chunk_z INT NOT NULL, " +
                        "owner_uuid TEXT NOT NULL, " +
                        "PRIMARY KEY (world, chunk_x, chunk_z));");
            }
            System.out.println("[HyAstur-Claims] Base de datos SQLite conectada correctamente en: " + dbPath);
        } catch (ClassNotFoundException e) {
            System.err.println("[HyAstur-Claims] Error crítico: No se encontró la clase del Driver de SQLite en el classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[HyAstur-Claims] Error SQL al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[HyAstur-Claims] Base de datos desconectada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean claimChunk(String world, int x, int z, UUID ownerUuid) {
        String sql = "INSERT OR REPLACE INTO claims(world, chunk_x, chunk_z, owner_uuid) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, world);
            pstmt.setInt(2, x);
            pstmt.setInt(3, z);
            pstmt.setString(4, ownerUuid.toString());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unclaimChunk(String world, int x, int z) {
        String sql = "DELETE FROM claims WHERE world = ? AND chunk_x = ? AND chunk_z = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, world);
            pstmt.setInt(2, x);
            pstmt.setInt(3, z);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getClaimOwner(String world, int x, int z) {
        String sql = "SELECT owner_uuid FROM claims WHERE world = ? AND chunk_x = ? AND chunk_z = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, world);
            pstmt.setInt(2, x);
            pstmt.setInt(3, z);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("owner_uuid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getClaimCount(String world, UUID playerUuid) {
        String sql = "SELECT COUNT(*) FROM claims WHERE world = ? AND owner_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, world);
            pstmt.setString(2, playerUuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getPlayerClaimsList(String world, UUID playerUuid) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT chunk_x, chunk_z FROM claims WHERE world = ? AND owner_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, world);
            pstmt.setString(2, playerUuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int x = rs.getInt("chunk_x");
                    int z = rs.getInt("chunk_z");
                    list.add("(" + x + ", " + z + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
