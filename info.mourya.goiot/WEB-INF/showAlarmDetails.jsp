
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>

<title>Alarm details</title>
</head>

<body>

 

 {"${successMsg}
<c:forEach items="${medicineDetailsNTList}" var="medcineName">
   ${medcineName},
</c:forEach>
"}

</body>
</html>