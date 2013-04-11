<!DOCTYPE html>
<%@page import="java.text.*"%>
<%@page import="de.oglimmer.bcg.logic.GameManager"%>
<%@page import="de.oglimmer.bcg.logic.Game"%>
<%@page import="de.oglimmer.bcg.logic.Player"%>
<%@page import="java.util.*"%>
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8"
	session="true"%>	
<%
	DateFormat df = DateFormat.getDateTimeInstance();
%>
<html>
<head>
<title>Browser Card Game</title>
<meta charset="utf-8" />
</head>
<body>

	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		ADMIN CONSOLE
	</div>


	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		Games waiting for a second player: (Name, Created, LastAccess, Email)<br/>
		<select id="gameListOpen">
		<% 
			for(Game g: GameManager.INSTANCE.getOpenGames()) { 
				String emails = "(";
				for(Player p : g.getPlayers().getPlayers()) {
					emails += p.getKey();
				}
				emails += ")";
		%>
			<option value="<%=g.getId()%>"><%= g.getName()+" / "+g.getType()+" / "+df.format(g.getCreated())+" / "+df.format(g.getLastAccess())+" / "+emails%></option>
		<% } %>
		</select>
		<button onclick="document.location.href='adminDel.htm?pass=<%=request.getParameter("pass") %>&gameId='+document.getElementById('gameListOpen').value;">Del game</button>
		<br/><br/>
		Games currently in progress: (Name, Created, LastAccess, Currently Connected, Emails)<br/>
		<select id="gameListRunning">
		<% 
			for(Game g: GameManager.INSTANCE.getRunningGames()) {
				String emails = "(";
				for(Player p : g.getPlayers().getPlayers()) {
					if(emails.length() > 1) {
						emails +=", ";
					}
					emails += p.getKey();
				}
				emails += ")";
		%>
			<option value="<%=g.getId()%>"><%= g.getName()+" / "+g.getType()+" / "+df.format(g.getCreated())+" / "+df.format(g.getLastAccess())+" / "+g.getPlayers().getCurrentlyConnectec()+" / "+emails%></option>
		<% } %>
		</select>
		<button onclick="document.location.href='adminDel.htm?pass=<%=request.getParameter("pass") %>&gameId='+document.getElementById('gameListRunning').value;">Del game</button>
		<br/><br/><br/>
		<a href="javascript:document.location.href='admin.htm?pass=<%=request.getParameter("pass") %>';">here</a> to refresh
	</div>
</body>
</html>
