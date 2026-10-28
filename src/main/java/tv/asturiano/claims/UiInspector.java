package tv.asturiano.claims;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UiInspector {
    public static void inspect(Object sender) {
        if (sender == null) {
            System.out.println("[HyAstur-Inspector] El objeto sender es nulo.");
            return;
        }

        System.out.println("[HyAstur-Inspector] ========================================");
        System.out.println("[HyAstur-Inspector] INICIO DE INSPECCIÓN PROFUNDA");
        System.out.println("[HyAstur-Inspector] Clase de Sender: " + sender.getClass().getName());
        System.out.println("[HyAstur-Inspector] ========================================");

        // 1. Inspeccionar métodos de Sender (incluyendo declarados/privados)
        inspectMethods(sender.getClass(), "SENDER");

        // 2. Inspeccionar campos (variables) de Sender para ver si almacena sesiones o contextos
        inspectFields(sender, sender.getClass(), "SENDER");

        try {
            // 3. Localizar y volcar el PacketHandler
            for (Method m : sender.getClass().getMethods()) {
                if (m.getName().equals("getPacketHandler") && m.getParameterCount() == 0) {
                    m.setAccessible(true);
                    Object handler = m.invoke(sender);
                    if (handler != null) {
                        System.out.println("\n[HyAstur-Inspector] ----------------------------------------");
                        System.out.println("[HyAstur-Inspector] CLASE DE PACKET_HANDLER: " + handler.getClass().getName());
                        System.out.println("[HyAstur-Inspector] ----------------------------------------");
                        inspectMethods(handler.getClass(), "PACKET_HANDLER");
                        inspectFields(handler, handler.getClass(), "PACKET_HANDLER");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[HyAstur-Inspector] Error inspeccionando PacketHandler: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[HyAstur-Inspector] ========================================");
        System.out.println("[HyAstur-Inspector] FIN DE INSPECCIÓN PROFUNDA");
        System.out.println("[HyAstur-Inspector] ========================================");
    }

    private static void inspectMethods(Class<?> clazz, String label) {
        System.out.println("[HyAstur-Inspector] --- MÉTODOS DE " + label + " (" + clazz.getName() + ") ---");
        // Usamos getDeclaredMethods para ver absolutamente todo (públicos, privados, protegidos)
        Class<?> current = clazz;
        while (current != null && !current.equals(Object.class)) {
            for (Method m : current.getDeclaredMethods()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[HyAstur-Inspector] [").append(label).append("] Method -> ")
                  .append(m.getModifiers()).append(" ")
                  .append(m.getReturnType().getSimpleName()).append(" ")
                  .append(m.getName()).append("(");
                
                Class<?>[] pTypes = m.getParameterTypes();
                for (int i = 0; i < pTypes.length; i++) {
                    sb.append(pTypes[i].getSimpleName());
                    if (i < pTypes.length - 1) sb.append(", ");
                }
                sb.append(")");
                System.out.println(sb.toString());
            }
            current = current.getSuperclass();
        }
    }

    private static void inspectFields(Object target, Class<?> clazz, String label) {
        System.out.println("[HyAstur-Inspector] --- CAMPOS/VARIABLES DE " + label + " (" + clazz.getName() + ") ---");
        Class<?> current = clazz;
        while (current != null && !current.equals(Object.class)) {
            for (Field f : current.getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                    Object val = f.get(target);
                    System.out.println("[HyAstur-Inspector] [" + label + "] Field -> " + f.getType().getSimpleName() + " " + f.getName() + " = " + (val != null ? val.getClass().getName() : "null"));
                } catch (Exception e) {
                    System.out.println("[HyAstur-Inspector] [" + label + "] Field -> " + f.getType().getSimpleName() + " " + f.getName() + " [Inaccesible]");
                }
            }
            current = current.getSuperclass();
        }
    }
}
