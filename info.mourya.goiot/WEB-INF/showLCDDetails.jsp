<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LCD Details</title>
</head>
<body>
${lcdMsg} 

{



"${patientDetailsObject.patientLName}"

<c:forEach items="${patientDetailsObject.medDetails}" var="medDetailsObject"> 

"${medDetailsObject.pillName}- 
${medDetailsObject.morning}-
${medDetailsObject.afternoon}-
${medDetailsObject.evening}"
</c:forEach>



}
</body>
</html>