<%@ taglib prefix="miniprofiler" uri="http://miniprofiler.com/jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>Simple Spring Web MVC application</title>

    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <miniprofiler:includes />
    <script type="text/javascript">
"use strict";

function sendRequest() {
    $.get("api/sum-n-difference", {x: 6, y: 8})
        .done(function(data) {
            $("#response").text(JSON.stringify(data));
        });
    $("#response").text("request in progress...");
}
    </script>

    <style>
        body {
            padding-left: 75px;  /* Move content from the edge so that MiniProfiler UI doesn't obscure it. */
        }
    </style>
</head>
<body>
    <p>
        Sample to show MiniProfiler usage in Spring Web MVC application.
    </p>

    <button onClick="sendRequest();">Send request</button>
    <br/>
    Response: <span id="response">request not sent</span>
</body>
</html>
