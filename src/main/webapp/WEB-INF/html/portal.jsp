<!DOCTYPE html>
<%@page import="de.oglimmer.bcg.logic.GameManager"%>
<%@page import="de.oglimmer.bcg.logic.Game"%>
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8"
	session="true"%>
<html>
<head>
<title>Browser Card Game</title>
<meta charset="utf-8" />
</head>
<body>

	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		Welcome	<%=session.getAttribute("email")%>
	</div>


	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		To start a new game click <button onclick="document.location.href='start.htm'">here</button><br/><br/><br/>
		
		To join another game select one 
		<select id="gameList">
		<% for(Game g: GameManager.INSTANCE.getAllGames()) { %>
			<option value="<%=g.getId()%>"><%= g.getId()%></option>
		<% } %>
		</select>
		and click <button	onclick="if(document.getElementById('gameList').value!='') document.location.href='join.htm?gameId='+document.getElementById('gameList').value; else alert('No game selected!');">here</button> or <a href="javascript:window.location.reload();">here</a> to refresh
	</div>
</body>
</html>
