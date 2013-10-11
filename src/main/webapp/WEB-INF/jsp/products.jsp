<%@ page import="java.io.PrintWriter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <style type="text/css">
        fieldset {
            border: 1px solid
        }

        legend {
            border: 1px solid;
            text-align: left;
        }

        label {
            width:125px;
            display: inline-block;
            vertical-align: top;
        }

        input {
            display: inline-block;
        }

    </style>
</head>
<body>

<%
    if (request.getAttribute("throwable") != null) {
        Throwable t = (Throwable) request.getAttribute("throwable");
%><p>An exception occurred: <strong><code><%= t.getMessage() %>
</code></strong></p><%
    }
%>

<h1>Add Product</h1>

<fieldset>
    <legend>Product</legend>
    <form action="${pageContext.request.contextPath}/product/upload" enctype="multipart/form-data" method="post">

        <div>
            <label for="productName">Product name:</label>
            <input type="text" name="productName" id="productName"/>
        </div>

        <div>
            <label for="productName">Image:</label>
            <input type="file" id="imageFile" name="imageFile"/>
        </div>
        <div>
            <label for="productName">Image credits:</label>
            <input type="text" name="imageCredits" id="imageCredits"/>
        </div>
        <div>
            <input type="submit" name="add" value="Add new product"/>
        </div>
    </form>
</fieldset>


<h1>Products</h1>
<table border="1">
    <thead>
    <tr>
        <th>Name</th>
        <th>Picture</th>
        <th>Picture Credits</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${products}" var="product">
        <tr>
            <td>${product.name}</td>
            <td><img src="${product.imageUrl}"/></td>
            <td>${product.imageCredits}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%
    if (request.getAttribute("throwable") != null) {
        Throwable t = (Throwable) request.getAttribute("throwable");
%><h1>Detailed exception</h1>
<code><pre><%
    t.printStackTrace(new PrintWriter(out));
%></pre>
</code><%
    }
%>
</body>
</html>