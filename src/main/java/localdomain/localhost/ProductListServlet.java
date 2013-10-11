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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/product/list", loadOnStartup = 1)
public class ProductListServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private EntityManagerFactory emf;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        emf = (EntityManagerFactory) config.getServletContext().getAttribute(EntityManagerFactory.class.getName());
        if (emf == null)
            throw new ServletException("JPA EntityManagerFactory not found in ServletContext");

        logger.debug("Servlet initialized, JPA EntityManagerFactory loaded");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager entityManager = emf.createEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT p FROM localdomain.localhost.domain.Product p");
            List products = query.getResultList();
            logger.debug("findAll(): {}", products);

            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/jsp/products.jsp").forward(req, resp);
        } finally {
            entityManager.close();
        }
    }
}
