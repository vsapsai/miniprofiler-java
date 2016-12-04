<%@ taglib prefix="miniprofiler" uri="http://miniprofiler.com/jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>Profiling AJAX requests</title>
    <link rel="stylesheet" href="style.css">

    <!-- Use jQuery because MiniProfiler UI integrates with various frontend
         libraries but not with raw XMLHttpRequest. -->
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <miniprofiler:includes />
    <script type="text/javascript">
"use strict";

function sendRequest() {
    $.ajax("ajax-handler")
        .done(function(data) {
            $("#response").text(JSON.stringify(data));
        });
    $("#response").text("request in progress...");
}
    </script>
</head>
<body>
    <a href="index.jsp">Home</a><br />
    <button onClick="sendRequest();">Send request</button>
    <br/>
    Response: <span id="response">request not sent</span>
</body>
</html>
