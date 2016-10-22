<%@ taglib prefix="miniprofiler" uri="http://miniprofiler.com/jsp" %>
<%@ page import="java.util.Random" %>
<%@ page import="com.miniprofiler.MiniProfiler" %>

<!DOCTYPE html>
<html>
<head>
    <title>Profiling with scriptlets</title>
    <link rel="stylesheet" href="style.css">

    <miniprofiler:includes />
</head>
<body>
    <a href="index.jsp">Home</a><br />
    Scriptlets usage is discouraged but it doesn't mean profiling shouldn't work with them. <br />

<%
AutoCloseable step = MiniProfiler.getCurrent().step("In Scriptlet");
Thread.sleep(100 + new Random().nextInt(400));
step.close();
%>

</body>
</html>
