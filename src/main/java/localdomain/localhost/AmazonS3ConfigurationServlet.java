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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(value = "/amazonS3/configuration")
public class AmazonS3ConfigurationServlet extends HttpServlet {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        try {
            String accessKey = request.getParameter("accessKey");
            String secretKey= request.getParameter("secretKey");
            String bucketName= request.getParameter("bucket");

            AmazonS3Resources s3Resources = new AmazonS3Resources(accessKey, secretKey, bucketName);
            logger.info("Amazon S3 configuration successful");

            // store AmazonS3Resources instance in the servlet context to share it with the servlet
            // as we don't use a dependency injection framework in this sample
            ServletContext context = getServletContext();
            context.setAttribute(AmazonS3Resources.class.getName(), s3Resources);

            response.sendRedirect(request.getContextPath() + "/product/list");

        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

}
