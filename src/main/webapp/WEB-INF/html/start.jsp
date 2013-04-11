<!DOCTYPE html>
<%@page import="java.util.Collection"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8"
	session="true"%>
<html>
<head>
<title>Browser Card Game</title>
<meta charset="utf-8" />
</head>
<body>

	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		New Game <%=request.getParameter("gametype") %>
	</div>

	<div style="border:3px solid black;padding:5px;font-family:Arial;margin-bottom:5px;">
		Select a deck from your saved decks:<br/>
		<select id="deckList">
		<% 
			boolean validDeckFound = false;
			JSONArray decksList = (JSONArray) request.getAttribute("deckList");
			if(decksList!=null) {
				for(JSONObject deck : (Collection<JSONObject>)decksList) {
					if(deck.getBoolean("valid")) {
						validDeckFound = true;
		%>
			<option value="<%=deck.getString("id")%>"><%= deck.getString("name")+" ("+deck.getString("side")+")"%></option>
		<% 
					}
				}
			}
		%>
		</select><br/><br/>
		<% if (!validDeckFound) { %>
			You don't have a valid deck. <a href="portal.htm">Back</a>
		<% } else { %>
		Click <button	onclick="document.location.href='prepare.htm?gametype=<%=request.getParameter("gametype")%>&deckId='+document.getElementById('deckList').value;">here</button> and wait for another player to join.
		<% } %>
	</div>
</body>
</html>
