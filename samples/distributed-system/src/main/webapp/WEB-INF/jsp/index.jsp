<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="miniprofiler" uri="http://miniprofiler.com/jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>Multi-Service Distributed Web Application</title>

    <miniprofiler:includes />

    <style>
        body {
            padding-left: 75px;  /* Move content from the edge so that MiniProfiler UI doesn't obscure it. */
        }
    </style>
</head>
<body>
    Random Wikipedia article: ${article}
</body>
</html>

