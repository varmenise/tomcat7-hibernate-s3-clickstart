<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en">
<body>

<h1>Add Product</h1>

<form action="/product/upload" enctype="multipart/form-data" method="post">
    <table>
        <tr>
            <td> Product name: </td>
            <td> <input type="text" name="productName" id="productName" /> </td>
        </tr>

        <tr>
            <td> Image: </td>
            <td> <input type="file" id="imageFile" name="imageFile" /> </td>
        </tr>

        <tr>
            <td> Image credits: </td>
            <td> <input type="text" name="imageCredits" id="imageCredits" /> </td>
        </tr>

        <tr>
            <td>&nbsp;</td>
            <td align ="left"> <input type="submit" name="add" value="Add new item" /> </td>
        </tr>
    </table>
</form>

<h1>Products</h1>
<table>
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
</body>
</html>