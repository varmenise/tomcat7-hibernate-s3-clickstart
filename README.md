Welcome to Amazon S3 ClickStart on CloudBees

This is a "ClickStart" that gets you going with a simple Maven Amazon S3 "seed" project starting point, which will show you how to upload an image to a permanent filesysten in Amazon S3. You can launch it here:

<a href="https://grandcentral.cloudbees.com/?CB_clickstart=https://raw.github.com/fbelzunc/tomcat7-hibernate-s3-clickstart/master/clickstart.json"><img src="https://d3ko533tu1ozfq.cloudfront.net/clickstart/deployInstantly.png"/></a>

This will setup a continuous deployment pipeline - a CloudBees Git repository, a Jenkins build compiling and running the test suite (on each commit).
Should the build succeed, this seed app is deployed on a Tomcat 7 container.

# CloudBees Tomcat 7 container

Tomcat 7 container is available on CloudBees thanks to the [tomcat7-clickstack](https://github.com/CloudBees-community/tomcat7-clickstack). Documentation is available [here](https://developer.cloudbees.com/bin/view/RUN/Tomcat7).

# How to deploy a web application on a Tomcat7 ClickStack

You can deploy your web application on the tomcat7 clickstack using the [CloudBees SDK](https://developer.cloudbees.com/bin/view/RUN/BeesSDK) "`app:deploy`" command.

```
bees app:deploy -a myapp -t tomcat7 ./target/tomcat7-maven-clickstart-1.0-SNAPSHOT.war
```

* "`-a myapp`": name of the CloudBees account and of the application. The application will be accessible on the URL http://tomcat7-maven-clickstart.cyrille-leclerc.cloudbees.net/
* "`-t tomcat7`": identifier of the tomcat7 clickstack
* "`./target/tomcat7-maven-clickstart-1.0-SNAPSHOT.war`": path to the war file.
You only need to set the "`-R`", "`-t`" and "`-D`" settings once - they will be remembered for subsequent deploys.

# How to bind a CloudBees MySql database to an application on a Tomcat7 ClickStack

## Create database if needed
```
db:create --username my-username --password alpha-beta mydb
```

## Bind application to database

```
bees app:bind -a  myapp -db mydb -as mydb
```
* "`-a  myapp`": the name of your application
* "`-db mydb`": the name of your CloudBees MySQL Database
* "`-as mydb`": the name of the binding which is used to identify the binding and to compose the name of the environment variables used to describe this binding (always prefer '_' to '-' for bindings because '-' is not supported in linux environment variable names).

This binding will create

* A JNDI DataSource with name "`java:comp/env/jdbc/mydb`" (also available at "`jdbc/mydb`")
* The following System Properties
  * `DATABASE_URL_MYDB`: url of the database starting with "mysql:" (e.g. "mysql://ec2-1.2.3.4.compute-1.amazonaws.com:3306/tomcat7-maven-clickstart-db"). **Please note** that this URL is **not** prefixed by "jdbc:".
  * `DATABASE_USERNAME_MYDB`: login of the database
  * `DATABASE_PASSWORD_MYDB`: password of the database

Details on bindings are available in [Binding services (resources) to applications](https://developer.cloudbees.com/bin/view/RUN/Resource+Management).

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

You can now use your "`java:comp/env/jdbc/mydb`" JNDI DataSource in your application.
Please note that "`jdbc/mydb`" is also available.

Java code sample:

```java
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/mydb");
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
<sql:query var="rs" dataSource="jdbc/mydb">
    select 1 as col1
</sql:query>

<h1>Datasource JSTL Demo</h1>

<c:forEach var="row" items="${rs.rows}">
Row: ${row.col1}<br/>
</c:forEach>
```


 




