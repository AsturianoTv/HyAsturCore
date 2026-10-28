package tv.asturiano.claims;

import java.lang.reflect.Method;

public class PacketHandlerInspector {
    public static void inspect(Object packetHandler) {
        System.out.println("========== [PacketHandler-Inspector] INICIO ==========");
        if (packetHandler == null) {
            System.out.println("PacketHandler es nulo.");
            return;
        }

        Class<?> clazz = packetHandler.getClass();
        System.out.println("Clase del PacketHandler: " + clazz.getName());

        for (Method m : clazz.getMethods()) {
            String name = m.getName().toLowerCase();
            // Buscamos cualquier método relacionado con escritura, paquetes, envío o canales
            if (name.contains("packet") || name.contains("write") || name.contains("send") || name.contains("ui") || name.contains("window")) {
                System.out.println("Método: " + m.getName() + " | Params: " + m.getParameterCount());
                for (Class<?> pType : m.getParameterTypes()) {
                    System.out.println("   -> Param type: " + pType.getName());
                }
            }
        }
        System.out.println("========== [PacketHandler-Inspector] FIN ==========");
    }
}
