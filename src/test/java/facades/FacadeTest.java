package facades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

class FacadeTest {

    private static EntityManagerFactory emf;
    private static Facade facade;

    public FacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = Facade.getFacade(emf);
    }

    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();

    }

    @AfterEach
    void tearDown() {
        EntityManager em = emf.createEntityManager();

    }

    @Test
    void name() {
        // TODO: Remember to add Secrets for Github Actions
    }
}