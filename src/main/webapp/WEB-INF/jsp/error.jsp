<!DOCTYPE html>
<html lang="en">
<head>
    <title>Error webpage</title>
</head>
<body>

<% out.print(request.getAttribute("error")); %> Click <a href="<%=request.getAttribute("back")%>">here</a> to go back to
the main page.

</body>
</html>