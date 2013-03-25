define([ "dojo/_base/declare", "dojo/_base/lang", "dojo/window" ], function(declare, lang, window) {

	return declare(null, {

		socket : null,
		queryObject : null,

		constructor : function() {
		},
		
		sendShuffle: function(v) {
			this.sendMsg("shuffle", {
				deckId : v.id
			});
		},
		
		sendReturnToDeck: function(v) {
			this.sendMsg("returnToDeck", {
				cardId : v.id,
				location: v.param
			});
		},
		
		sendModCounter: function(v) {
			this.sendMsg("modCounter", {
				cardId : v.id,
				mode: v.param
			});
		},
		
		sendPing: function() {
			this.sendMsg("ping", {});
			setTimeout(this.sendPing.bind(this), 1000*60);
		},
		
		sendDiscardCard: function(v) {
			this.sendMsg("discard", {
				cardId : v.id
			});
		},
		
		sendFlipCard: function(v) {
			this.sendMsg("flipCard", {
				cardId : v.id
			});
		},
		
		sendTakeCardPlayOnTable: function(v) {
			this.sendMsg("takeCardPlayOnTable", {
				deckId : v.id,
				faceup: v.param
			});
		},

		sendRotateCard : function(v) {
			this.sendMsg("rotateCard", {
				cardId : v.id
			});
		},

		sendPlayCardOnTable : function(v) {
			this.sendMsg("playCardOnTable", {
				cardId : v.id,
				faceup: v.param
			});
		},

		sendMoveCardMsg : function(v, xPos, yPos) {
			this.sendMsg("moveCard", {
				cardId : v.id,
				xPos : xPos,
				yPos : yPos
			});
		},

		sendTakeCardIntoHand : function(v) {
			this.sendMsg("takeCardIntoHand", {
				id : v.id,
				sourceType: v.param
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