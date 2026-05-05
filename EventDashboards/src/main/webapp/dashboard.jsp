<%@ page session="true" %>

<html>
<head>
<title>Dashboard</title>
</head>

<body>

<h2>Welcome to Event Dashboard</h2>

<p>User: <%= session.getAttribute("username") %></p>

<a href="createEvent.jsp">Create Event</a><br>
<a href="viewEvents.jsp">View Events</a><br>

</body>
</html>