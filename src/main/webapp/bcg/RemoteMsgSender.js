define([ "dojo/_base/declare", "dojo/_base/lang", "dojo/window" ], function(declare, lang, window) {

	return declare(null, {

		socket : null,
		queryObject : null,

		constructor : function() {
		},
		
		send: function(actionName, v) {
			if(actionName == "ping" || actionName == "moveCardMsg" || actionName == "initMsg" || actionName == "preInitMsg") {
				throw "send must not be called for action="+actionName;
			} else {
				this.sendMsg(actionName, {
					entityId : v.id,
					param : v.param
				});
			}
		},
		
		sendPing: function() {
			this.sendMsg("ping", {});
			setTimeout(this.sendPing.bind(this), 1000*60);
		},
		
		sendMoveCardMsg : function(v, xPos, yPos) {
			this.sendMsg("moveCard", {
				id : v.id,
				xPos : xPos,
				yPos : yPos
			});
		},
		
		sendInitMsg : function() {
			this.sendMsg("init", {});
		},

		sendPreInitMsg : function() {
			var vs = window.getBox();
			this.sendMsg("preinit", {
				browserWidth: vs.w,
				browserHeight: vs.h
			});			
		},		

		sendMsg : function(actionId, additionalParams) {
			var self = this;
			var msg = {
				actionId : actionId,
				gameId : self.queryObject.gameId,
				playerId : self.queryObject.playerId,
			};
			lang.mixin(msg, additionalParams);
			this.socket.send(JSON.stringify(msg));
		}
	});

});