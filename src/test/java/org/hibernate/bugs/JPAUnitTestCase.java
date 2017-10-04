package org.hibernate.bugs;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.hibernate.bugs.models.Bar;
import org.hibernate.bugs.models.Foo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        // setup some test data...
        for (int i = 1; i < 2; i++) {
            Foo f = new Foo();
            f.setId(i);
            f.setName("Foo " + i);
            entityManager.persist(f);
        }

        // ...now the test entity w/ the formula
        Bar b = new Bar();
        b.setId(1);
        entityManager.persist(b);

        entityManager.getTransaction().commit();
        entityManager.close();
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

    @Test
    public void filterParamInFormulaWithFindTest() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        // setup filter with param "id" = 1
        entityManager.unwrap(Session.class).enableFilter("fooFilter").setParameter("id", 1);

        // get entity by id -> filter in formula works!
        Bar b = entityManager.find(Bar.class, 1);
        assertEquals("Foo 1", b.getFooName());

        entityManager.getTransaction().commit();
		entityManager.close();
	}

    @Test
    public void filterParamInFormulaWithJQLTest() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        // setup filter with param "id" = 1
        entityManager.unwrap(Session.class).enableFilter("fooFilter").setParameter("id", 1);

        // get via HQL/JQL -> filter param isn't applied in formula
        Bar b = (Bar) entityManager.createQuery("select b from Bar b where b.id = :id").setParameter("id", 1).getSingleResult();
        assertEquals("Foo 1", b.getFooName());
        
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    public void filterParamInFormulaWithCriteriaTest() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        // setup filter with param "id" = 1
        entityManager.unwrap(Session.class).enableFilter("fooFilter").setParameter("id", 1);

        // get via criteria API -> filter param isn't applied in formula
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bar> q = cb.createQuery(Bar.class);
        Root<Bar> root = q.from(Bar.class);
        q.select(root).where(cb.equal(root.get("id"), 1));
        Bar b = entityManager.createQuery(q).getSingleResult();
        assertEquals("Foo 1", b.getFooName());

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
