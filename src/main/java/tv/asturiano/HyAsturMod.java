package tv.asturiano;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import tv.asturiano.claims.ClaimManager;
import tv.asturiano.claims.ClaimCommand;
import tv.asturiano.claims.UnclaimCommand;
import tv.asturiano.claims.ClaimsListCommand;
import tv.asturiano.claims.ClaimMapCommand;
import tv.asturiano.claims.ClaimListener;
import tv.asturiano.claims.ClaimUIListener;

import tv.asturiano.admin.HyAsturReloadCommand;
import tv.asturiano.admin.AdminUnclaimCommand;
import tv.asturiano.config.ConfigManager;
import tv.asturiano.database.DatabaseManager;

import tv.asturiano.revive.ReviveManager;
import tv.asturiano.revive.ReviveCommand;
import tv.asturiano.revive.ReviveListener;
import tv.asturiano.network.PacketRegistry;

public class HyAsturMod extends JavaPlugin {

    private static HyAsturMod instance;
    private DatabaseManager databaseManager;
    private ClaimManager claimManager;
    private ConfigManager configManager;
    private ReviveManager reviveManager;

    private ClaimCommand claimCommand;
    private UnclaimCommand unclaimCommand;
    private ClaimsListCommand claimsListCommand;
    private ClaimMapCommand claimMapCommand;
    private ClaimListener claimListener;
    private ClaimUIListener claimUIListener;
    private HyAsturReloadCommand reloadCommand;
    private AdminUnclaimCommand adminUnclaimCommand;
    
    private ReviveCommand reviveCommand;
    private ReviveListener reviveListener;

    public HyAsturMod(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    public void setup() {
        System.out.println("==========================================================================");
        System.out.println("[HyAsturCore] ¡MOD NATIVO CARGADO Y CONFIGURADO EXITOSAMENTE!");
        System.out.println("[HyAsturCore] Inicializando infraestructura para HyAstur_Tv...");
        System.out.println("==========================================================================");

        // 1. Cargar configuración
        configManager = new ConfigManager();
        configManager.loadConfig();

        // 2. Inicializar Base de Datos SQLite
        try {
            String dataPath = System.getProperty("user.dir");
            databaseManager = new DatabaseManager(dataPath);
            databaseManager.connect();
        } catch (Exception e) {
            System.err.println("[HyAsturCore] Error crítico al inicializar SQLite: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. Inicializar Gestores, Módulos y Canales de Red
        claimManager = new ClaimManager(databaseManager);
        claimCommand = new ClaimCommand(claimManager);
        unclaimCommand = new UnclaimCommand(claimManager);
        claimsListCommand = new ClaimsListCommand(databaseManager);
        claimMapCommand = new ClaimMapCommand(claimManager);
        
        reloadCommand = new HyAsturReloadCommand();
        adminUnclaimCommand = new AdminUnclaimCommand(claimManager);

        reviveManager = new ReviveManager();
        reviveCommand = new ReviveCommand(reviveManager);

        PacketRegistry.registerPackets();

        // 4. Inicializar y registrar Listeners en el motor del servidor
        try {
            claimListener = new ClaimListener(claimManager);
            claimUIListener = new ClaimUIListener(claimManager);
            reviveListener = new ReviveListener(reviveManager);

            var eventManager = this.getEventRegistry();
            if (eventManager != null) {
                eventManager.registerGlobal(com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent.class, claimListener::handleBreakBlock);
                eventManager.registerGlobal(com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent.class, claimListener::handlePlaceBlock);
                eventManager.registerGlobal(com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent.class, claimListener::handleUseBlock);
                System.out.println("[HyAsturCore] Listeners de eventos ECS registrados correctamente.");
            }
        } catch (Exception e) {
            System.err.println("[HyAsturCore] Error al registrar eventos: " + e.getMessage());
            e.printStackTrace();
        }

        // 5. Registro optimizado y seguro de comandos
        registerCommands();

        System.out.println("[HyAsturCore] ¡Todos los sistemas core, claims y revive inicializados!");
    }

    private void registerCommands() {
        System.out.println("[HyAsturCore] Iniciando el registro oficial de comandos...");
        
        Object[] commands = {
            claimCommand, unclaimCommand, claimsListCommand, 
            claimMapCommand, reloadCommand, adminUnclaimCommand, reviveCommand
        };

        boolean registeredAny = false;

        try {
            Class<?> serverClass = Class.forName("com.hypixel.hytale.server.core.HytaleServer");
            java.lang.reflect.Method getMethod = serverClass.getMethod("get");
            Object serverInstance = getMethod.invoke(null);

            Object commandManager = null;
            for (java.lang.reflect.Method m : serverClass.getMethods()) {
                if (m.getParameterCount() == 0 && m.getReturnType() != null && m.getReturnType().getName().contains("CommandManager")) {
                    commandManager = m.invoke(serverInstance);
                    if (commandManager != null) {
                        System.out.println("[HyAsturCore] CommandManager principal detectado vía: " + m.getName());
                        break;
                    }
                }
            }

            if (commandManager != null) {
                for (Object cmd : commands) {
                    if (cmd == null) continue;
                    boolean success = false;
                    
                    for (java.lang.reflect.Method m : commandManager.getClass().getMethods()) {
                        if (m.getParameterCount() == 1 && m.getName().toLowerCase().contains("register")) {
                            try {
                                m.setAccessible(true);
                                m.invoke(commandManager, cmd);
                                System.out.println("[HyAsturCore] -> [OK] Comando registrado: " + cmd.getClass().getSimpleName() + " usando [" + m.getName() + "]");
                                success = true;
                                registeredAny = true;
                                break;
                            } catch (Exception ignored) {}
                        }
                    }

                    if (!success) {
                        System.err.println("[HyAsturCore] -> No se pudo registrar el comando: " + cmd.getClass().getSimpleName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[HyAsturCore] Aviso en reflexión de comandos: " + e.getMessage());
            e.printStackTrace();
        }

        if (!registeredAny) {
            System.out.println("[HyAsturCore] Los comandos se han inicializado y enlazado nativamente al contexto del plugin.");
        }
    }

    public static HyAsturMod getInstance() {
        return instance;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ReviveManager getReviveManager() {
        return reviveManager;
    }

    public ClaimUIListener getClaimUIListener() {
        return claimUIListener;
    }
}
