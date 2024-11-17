package dashstar.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.reflect.Field;

public class HibernateUtil {
    private static EntityManagerFactory emf;
    public static void init() {
        emf = Persistence.createEntityManagerFactory("default");
    }
    public static void copyNonNullProperties(Object source, Object target) throws IllegalAccessException {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (value != null) {
                field.set(target, value);
            }
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}