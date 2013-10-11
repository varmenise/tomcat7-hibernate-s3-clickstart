/*
 * Copyright 2010-2013, the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package localdomain.localhost;

import localdomain.localhost.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;

/**
 * In charge of the lifecycle of the application components.
 * <p/>
 * As we don't use any dependency injection framework in this sample (Java EE CDI, Spring Framework, Google Guice, ...),
 * we need to manage the instantiation and
 * <p/>
 * <ul>
 * <li>Initialise the JPA {@link EntityManagerFactory}.</li>
 * <li>Inject demo data</li>
 * </ul>
 *
 */

@WebListener
public class ApplicationWebListener implements ServletContextListener {
    private static EntityManagerFactory entityManagerFactory;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // CREATE JPA ENTITY MANAGER FACTORY
        entityManagerFactory = Persistence.createEntityManagerFactory("localdomain.localhost");
        servletContextEvent.getServletContext().setAttribute(EntityManagerFactory.class.getName(), entityManagerFactory);
        logger.debug("JPA EntityManagerFactory created");

        // DEMO INSERT DEMO DATA
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createQuery("select p from Product p");
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            em.getTransaction().begin();
            em.persist(new Product(
                    "Long Island Iced Tea",
                    "http://bees-shop.s3-website-us-east-1.amazonaws.com/340757408_d3cbdba2f2.jpg",
                    "http://www.flickr.com/photos/alisdair/340757408/"));
            em.persist(new Product(
                    "Sex on the beach",
                    "http://bees-shop.s3-website-us-east-1.amazonaws.com/5115940004_2825a4548e.jpg",
                    "http://www.flickr.com/photos/elv/5115940004/"));
            em.getTransaction().commit();
            logger.info("Demo products inserted in the database");
        } else {
            logger.info("Don't insert demo products in the database, {} products already found in the db", resultList.size());
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            logger.debug("JPA EntityManagerFactory closed");
        }
    }
}
