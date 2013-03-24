<!DOCTYPE html>
<html>
	<head>
		<title>Browser Card Game</title>
		<meta charset="utf-8" />
	</head>
<body>
	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		Online came game assistance
	</div>
	<% if(request.getAttribute("reason")!=null) { %>
	<h1 style="border:3px solid red;padding:5px;font-family:Arial;"><%=request.getAttribute("reason") %></h1>
	<% } %>
	<div style="border:3px solid black;padding:5px;font-family:Arial">
		
		<form action="login.htm" method="POST">		
			Email:<br/><input type="text" name="email" /><br/>
			Password:<br/><input type="password" name="password" /><br/>
			<input type="submit" value="Login" />
		</form>
	</div>
</body>
</html>