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
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
</head>
<body>

	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		Welcome	<%=session.getAttribute("email")%> <button onclick="document.location.href='portal.htm?logoff=true';">Log off</button>
	</div>


	<div id="mainDiv" style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		<% if( session.getAttribute("permissionStartGame") != null && (Boolean)session.getAttribute("permissionStartGame") ) { %>
		To start a new
		<select id="gametype">
			<option value="swlcg">SWLCG</option>
			<option value="swccg">SWCCG</option>
		</select>
		click <button onclick="document.location.href='start.htm?gametype='+document.getElementById('gametype').value">here</button><br/><br/><br/>
		<% } else { %>
		You are not authorized to start a game, but you can wait until somebody else created one.<br/><br/><br/>
		<% } %>
		
		To join another game select one 
		<select id="gameList">
		<% for(Game g: GameManager.INSTANCE.getOpenGames()) { %>
			<option value="<%=g.getId()%>"><%= g.getName()+" ("+g.getType()+")"%></option>
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
			Click <button onclick="document.location.href='game.htm?gameId=<%=p.getGame().getId()%>&playerId=<%=p.getId()%>'">here</button> to rejoin <%=p.getGame().getName()+" ("+p.getGame().getType()+")" %> (created: <%=df.format(p.getGame().getCreated()) %>)<br/>
			<%	} %>
		<%	} %>
	</div>
	<script type="text/javascript">
	$(function() {
		if ($.browser.opera || $.browser.msie) {
			$("#mainDiv").html("Your browser sucks. Please use Chrome, Firefox or Safari.");
		}
	});
	</script>
</body>
</html>
