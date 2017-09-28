package calendarioacademico.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author cnmoro
 */
public class EManager implements java.io.Serializable {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("CalendarioPU");
    private static EntityManager em = emf.createEntityManager();

    public EManager() {
    }

    public static EntityManager getInstance() {
        if (em == null) {
            em = emf.createEntityManager();
        }
        return em;
    }

}
