Welcome to Amazon S3 ClickStart on CloudBees

This is a "ClickStart" that gets you going with a simple Maven Amazon S3 "seed" project starting point, which will show you how to upload an image to a permanent filesysten in Amazon S3. You can launch it here:

<a href="https://grandcentral.cloudbees.com/?CB_clickstart=https://raw.github.com/Cloudbees-community/tomcat7-hibernate-s3-clickstart/master/clickstart.json"><img src="https://d3ko533tu1ozfq.cloudfront.net/clickstart/deployInstantly.png"/></a>

This will setup a continuous deployment pipeline - a CloudBees Git repository, a Jenkins build compiling and running the test suite (on each commit).
Should the build succeed, this seed app is deployed on a Tomcat 7 container.

## Application Overview

### Step 1: Amazon S3 Configuration ###

You will need to enter your AWS credentials and also the bucket name in which you would like to upload your images. We also provide a Amazon S3 Bucket Configuration Tip to allow anonymous read access to your Amazon S3 Bucket with a Bucket Policy.

<img alt="Bees Shop - CloudBees MySQL" src="https://raw.github.com/Cloudbees-community/tomcat7-hibernate-s3-clickstart/master/src/site/img/product-amazon-configuration.png" style="width: 70%;"/>

### Step 2: Add products ###

Once you are sucessfully authenticated on AmazonS3, you can now start adding new products to the list. You just need the product name, the image file and the credits of the image.

<img alt="Bees Shop - CloudBees MySQL" src="https://raw.github.com/Cloudbees-community/tomcat7-hibernate-s3-clickstart/master/src/site/img/product-addproduct.png" style="width: 70%;"/>

You also have a view of the current products which are available on your product list. By the default you will have two product added which are in a CloudBees bucket.

<img alt="Bees Shop - CloudBees MySQL" src="https://raw.github.com/Cloudbees-community/tomcat7-hibernate-s3-clickstart/master/src/site/img/product-productslist.png" style="width: 70%;"/>

# Create application manually

### Create Tomcat container

```sh
bees app:create -a product -t tomcat7
```

### Create CloudBees MySQL Database

```sh
bees db:create product-db
```

### Bind Tomcat container to database

```sh
bees app:bind -a product -db product-db -as product
```
### Deploy your application

```sh
bees app:deploy -a product -t tomcat7 app.war
```

### Configure JPA and Hibernate in your application

#### Declare Hibernate and JPA jars in your Maven pom.xml

```xml
<project ...>
    <dependencies>
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>persistence-api</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>4.2.4.Final</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>4.2.4.Final</version>
        </dependency>
        ...
    </dependencies>
</dependencies>
```

#### Declare persistence.xml in your classpath under META-INF

```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">

    <persistence-unit name="localdomain.localhost">
        <non-jta-data-source>java:comp/env/jdbc/mydb</non-jta-data-source>
        <class>localdomain.localhost.domain.Product</class>
        <properties>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL5InnoDBDialect"/>
        </properties>
    </persistence-unit>
</persistence>
```

Note:

* Use a `<non-jta-data-source>`, don't use the `<jta-data-source>` unless needed and configured properly with a JTA transaction manager (provided by your Java EE container, by Spring Framework, ...)
* Use a datasource JNDI name prefixed by `java:comp/env/`
* Properties prefixed by `hibernate.` are used to configure Hibernate, no need to add an additional `hibernate-cfg.xml` file in most cases

#### JPA EntityManagerFactory initialisation and lifecycle

As this sample don't use a Dependency Injection Framework (Java EE CDI, Spring Framework, Google Guice, ...), we have to manually initialise and close the
JPA `EntityManagerFactory`.

This lifecycle is done in `ApplicationWebListener`:

* `ApplicationWebListener` implements `ServletContextListener` to trap web application lifecycle events (`contextInitialized(ServletContextEvent)` and `contextDestroyed(ServletContextEvent)`)
* `@WebListener` annotation is used instead of declaring the `ApplicationWebListener` class in `web.xml`
* JPA `EntityManagerFactory` is initialised with

   ```
Persistence.createEntityManagerFactory("localdomain.localhost")
```

* The JPA `EntityManagerFactory` instance is shared with servlets storing it as a `ServletContext` attribute.


### Use the DataSource in you application

#### Plain Java

You can now use your "`java:comp/env/jdbc/product`" JNDI DataSource in your application.
Please note that "`jdbc/product`" is also available.

Java code sample:

```java
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/product");
Connection conn = ds.getConnection();
ResultSet rst = stmt.executeQuery("select 1");
while (rst.next()) {
    out.print("resultset result: " + rst.getString(1));
}
rst.close();
stmt.close();
conn.close();
```

#### Java Standard Tag Library / JSTL

JSP / JSTL code sample:

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<sql:query var="rs" dataSource="jdbc/product">
    select 1 as col1
</sql:query>

<h1>Datasource JSTL Demo</h1>

<c:forEach var="row" items="${rs.rows}">
Row: ${row.col1}<br/>
</c:forEach>
```


 




