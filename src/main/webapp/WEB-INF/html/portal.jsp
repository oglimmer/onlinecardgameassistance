<!DOCTYPE html>
<%@page import="java.text.*"%>
<%@page import="de.oglimmer.bcg.logic.GameManager"%>
<%@page import="de.oglimmer.bcg.logic.Game"%>
<%@page import="de.oglimmer.bcg.logic.Player"%>
<%@page import="java.util.*"%>
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
		<% if( (Boolean)session.getAttribute("permissionStartGame") ) { %>
		To start a new game click <button onclick="document.location.href='start.htm'">here</button><br/><br/><br/>
		<% } else { %>
		You are not authorized to start a game, but you can wait until somebody else created one.<br/><br/><br/>
		<% } %>
		
		To join another game select one 
		<select id="gameList">
		<% for(Game g: GameManager.INSTANCE.getOpenGames()) { %>
			<option value="<%=g.getId()%>"><%= g.getName()%></option>
		<% } %>
		</select>
		and click <button onclick="if(document.getElementById('gameList').value!='') document.location.href='join.htm?gameId='+document.getElementById('gameList').value; else alert('No game selected!');">here</button> or <a href="javascript:window.location.reload();">here</a> to refresh		
		<%
			DateFormat df = DateFormat.getDateTimeInstance();
			Collection<Player> playerInGames = GameManager.INSTANCE.getGamesRegistered((String) session.getAttribute("email"));
			if(!playerInGames.isEmpty()) {
		%>
			<br/><br/><br/>You are registered in running games:<br/>
			<% for(Player p : playerInGames) { %>
			Click <button onclick="document.location.href='game.htm?gameId=<%=p.getGame().getId()%>&playerId=<%=p.getId()%>'">here</button> to rejoin <%=p.getGame().getName() %> (created: <%=df.format(p.getGame().getCreated()) %>)<br/>
			<%	} %>
		<%	} %>
	</div>
</body>
</html>
