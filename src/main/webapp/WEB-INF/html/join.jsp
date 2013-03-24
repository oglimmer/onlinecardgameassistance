<!DOCTYPE html>
<%@page import="java.util.Collection"%>
<%@page import="de.oglimmer.bcg.logic.GameManager"%>
<%@page import="de.oglimmer.bcg.logic.Game"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8"
	session="true"%>
<%
	JSONArray decksList = (JSONArray) session.getAttribute("deckList");
	String gameId = request.getParameter("gameId");
	String otherSide = GameManager.INSTANCE.getGame(gameId)
			.getPlayers().getPlayer(0).getSide().toString();
%>
<html>
<head>
<title>Browser Card Game</title>
<meta charset="utf-8" />
</head>
<body>

	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		Join a game
	</div>

	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		Select a deck from your saved decks:<br/>
		<select id="deckList">
			<%
				boolean validDeckFound = false;
				for(JSONObject deck : (Collection<JSONObject>)decksList) {
					if(!deck.getString("side").equalsIgnoreCase(otherSide) && deck.getBoolean("valid")){
						validDeckFound = true;
			%>		
						<option value="<%=deck.getString("id")%>"><%= deck.getString("name")+" ("+deck.getString("side")+")"%></option>
			<%
					}
				}
			%>
		</select>
		<br/><br/>	
		<% if (!validDeckFound) { %>
			You don't have a valid deck for the opponent side. <a href="portal.htm">Back</a>
		<% } else { %>
			Click <button onclick="document.location.href='prepare.htm?gameId=<%=gameId%>&deckId='+document.getElementById('deckList').value;">here</button> to joing the game.
		<% } %>
	</div>

</body>
</html>
