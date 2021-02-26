package de.hsos.geois.ws2021.data;

import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerHandler {

    private static final EntityManagerFactory emf; 
    private static final ThreadLocal<EntityManager> threadLocal;

    static {
        emf = Persistence.createEntityManagerFactory("geois");      
        threadLocal = new ThreadLocal<EntityManager>();
    }

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();

        if (em == null) {
            em = emf.createEntityManager();
//            System.out.println("EntityManager angelegt" + em.toString());
            threadLocal.set(em);
        }
        return em;
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null) {
//        	System.out.println("EntityManager geschlossen" + em.toString());
            em.close();
            threadLocal.set(null);
        }
    }

    public static void closeEntityManagerFactory() {
        emf.close();
    }
    
    public static <T> T runInTransaction(Function<EntityManager, T> function) {
    	EntityManager entityManager = null;
		try {
			entityManager = EntityManagerHandler.getEntityManager();
			entityManager.getTransaction().begin();
			T result = function.apply(entityManager);
			entityManager.getTransaction().commit();
			return result;
		} finally {
			if (entityManager!=null) {
				EntityManagerHandler.closeEntityManager();
			}
		}
	} 
}
