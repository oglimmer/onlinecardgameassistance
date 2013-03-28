define([ "dojo/dom", "dojo/fx", "dojo/dom-construct", "dojox/css3/fx",
				"bcg/Moveable", "dojo/_base/declare", "dojo/_base/array", "dojo/on", 
				"dojo/query", "./MenuHandler", "dojo/window" ], 
function(dom, fx, domConstruct, cssFx, Moveable, declare, arrayUtil, on, query, MenuHandler, win) {
	
return declare(null, {
	
	remoteMsgSender: null,
	idFromMessages:null,
	menuHandler:null,
	
	constructor: function(remoteMsgSender) {
		this.remoteMsgSender = remoteMsgSender;
		this.menuHandler = new MenuHandler(remoteMsgSender);
	},
	
	errorHandler: function(v) {
		dom.byId("overlay").style.display = "block";
		dom.byId("overlayText").innerHTML = "Error:<br/><br/>"+v+"<br/><br/><a href='portal.htm'>Back to main screen</a>";
	},
	
	initHandler: function(v) {
		var borderNode = dom.byId("borderX");
		if(win.getBox().w > v.browserWidth) {
			borderNode.style.width = (v.browserWidth*1)+"px";
			borderNode.style.top = (v.browserHeight*0.76)+"px";
		} else {
			borderNode.style.width = "0px";
			borderNode.style.top = "0px";			
		}
		borderNode = dom.byId("borderY");
		if(win.getBox().h > v.browserHeight) {
			borderNode.style.left = (v.browserWidth*1)+"px";
			borderNode.style.height = (v.browserHeight*0.76)+"px";
		} else {
			borderNode.style.left = "0px";
			borderNode.style.height = "0px";			
		}
		
		// if the table is already populated, ignore an init msg		
		if(dom.byId("table").innerHTML == "") {
			this.remoteMsgSender.sendInitMsg();
			var self = this;
			on(window, "resize", Cowboy.debounce( 1000, function(e) {
				self.remoteMsgSender.sendPreInitMsg();
			}));
		}
	},
	
	toggleHighlightHandler: function(v) {
		var toHighlight = dom.byId(v.id);
		if(v.hl) {
			toHighlight.style.border = "3px solid yellow";
			toHighlight.style.boxShadow= "3px 3px 1px #888888";
		} else {
			toHighlight.style.border = "";
			toHighlight.style.boxShadow= "";
		}
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	removeHandler: function(v) {
		var toDestroy = dom.byId(v.id);
		if(toDestroy!==null) {
			domConstruct.destroy(toDestroy);
		}
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	modCounterHandler: function(v) {
		dom.byId("TXT"+v.id).innerHTML = this.getCounter(v);
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	infoHandler: function(v) {		
		if(typeof(v.infoText) != 'undefined') {
			dom.byId("info").innerHTML = v.infoText;
		}
	},
	
	messageHandler: function(v) {
		if(typeof(v.messageItem) != 'undefined') {
			domConstruct.create("li", {innerHTML: v.messageItem}, this.idFromMessages, "first");
		}
	},
	
	discardHandler: function(v) {
		var toDestroy = dom.byId(v.id);
		if(toDestroy!==null) {
			domConstruct.destroy(toDestroy);
		}
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	/* called from: FlipCardAction (your/other card), ReturnToDeckAction/TakeCardPlayOnTableAction/TakeCardIntoHandAction (deck) */
	updateImageHandler: function(v) {		
		var node = dom.byId(v.id);
		
		if(v.type != "deck") {
			cssFx.flip({
				node : node,
				duration: 100
			}).play();
		}
		
		dom.byId("IMG"+v.id).src = "cards/"+ v.imageUrl;
		
		this.menuHandler.removeMenu(v.id);
		
		this.menuHandler.addMenu(v, v.menu);
		
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	/* answer from sendRotateCard for your and opponents cards*/
	rotateCardHandler : function(v) {
		var node = dom.byId(v.cardId);
		if(node.bcgRotated=="1") {
			node.bcgRotated="0";
			cssFx.rotate({
				node : node,
				startAngle : "90deg",
				endAngle : "0deg",
				duration: 100
			}).play();
		} else {
			node.bcgRotated="1";
			cssFx.rotate({
				node : node,
				startAngle : "0deg",
				endAngle : "90deg",
				duration: 100
			}).play();
		}
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	/* answer from a mouse move for opponents cards */
	moveCardHandler: function(v) {
		fx.slideTo({
		    node: v.cardId,
		    top: v.yPos,
		    left: v.xPos,
		    units: "px"
		}).play();
	},
	
	/* answer from sendPlayCardOnTable for your cards */
	playCardHandler: function(v) {
		var node = dom.byId(v.id);
		this.menuHandler.removeMenu(v.id);
		
		node.style.left = v.x+"px";
		node.style.top = v.y+"px";
		domConstruct.place(node, v.areaId, "last");
		
		this.menuHandler.addMenu(v, v.menu);
		
		if(dom.byId("IMG"+v.id).src != "cards/"+v.imageUrl) {
			dom.byId("IMG"+v.id).src  = "cards/"+v.imageUrl;
		}
		
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	/* answer from takeCardIntoHand or sendPlayCardOnTable for opponents cards */	
	createCardHandler: function(v) {
		var areaNode = dom.byId(v.areaId);
		if(typeof(areaNode)=='undefined'||areaNode==null) {
			console.log("Unable to find areaId = "+v.areaId);
			return;
		}
		var card = this.createDivImage(v, areaNode);
		
		if(v.owner) {	
			new Moveable(card, v, this.remoteMsgSender.sendMoveCardMsg.bind(this.remoteMsgSender));
		}
		
		this.messageHandler(v);
		this.infoHandler(v);
	},
	
	/* answer from init */
	createDivsHandler: function(v) {
		var self = this;
		dom.byId("overlay").style.display = "none";
		dom.byId("table").innerHTML ="";
		arrayUtil.forEach(v, function(item, index) {
						
			var newDiv = domConstruct.create("div", {
				"class" : "area",
				"id" : item.id,
				"style" : item.css
			}, dom.byId("table"));
			
			if(item.id=='messages') {
				self.idFromMessages = item.id;
				newDiv.id = "messagesDiv";
				newDiv = domConstruct.create("ul", {
					id: item.id
				}, newDiv);
			} else {
				arrayUtil.forEach(item.cardDecks, function(deckItem, index) {
					self.createDivImage(deckItem, newDiv);
				});
			}
		});			
	},
	
	createCardZoom: function(c, v) {
		function removeZoomCard() {
			var toDestroy = dom.byId("ZOOMCARD");
			if(toDestroy!==null) {
				domConstruct.destroy(toDestroy);
			}
		}		
		on(c, "mouseover", function(e) {
			var imgNode = dom.byId("IMG"+v.id);
			if(imgNode!=null) {
				removeZoomCard();
				domConstruct.create("img", {				
					src : imgNode.src.replace(/cards/g, "cardsLarge"),
					id : "ZOOMCARD",
					style : {
						zIndex: 1,
						position : "absolute",
						top : "5px",
						right : "5px"
					}
				}, "table");
			}
		});
		on(c, "mouseout", function(e) {
			removeZoomCard();
		});	
	},
	
	createDivImage: function(v, areaNode) {
		var card = domConstruct.create("div", {			
			id : v.id,
			style : {
				zIndex: 2,
				position : "absolute",
				top : v.y+"px",
				left : v.x+"px",
				borderRadius: "5px"				
			}
		}, areaNode);
		domConstruct.create("img", {
			src : "cards/"+v.imageUrl,
			id: "IMG"+v.id			
		}, card);
		domConstruct.create("div", {
			innerHTML: this.getCounter(v),
			id: "TXT"+v.id,
			style : {				
				position: "absolute",
				left: ((typeof(v.counterPosX) != 'undefined')?v.counterPosX:"8")+"px",
				top: ((typeof(v.counterPosY) != 'undefined')?v.counterPosY:"25")+"px",
				width: "60px",
				fontWeight: "bolder",
				fontSize: "20px",
				fontFamily: "Arial",
				textShadow:	"0 1px 0 #E2007A,0 -1px 0 #E2007A,1px 0 0 #E2007A,-1px 0 0 #E2007A",
				color:"#000"
			}
		}, card);
		
		if(v.hl) {
			card.style.border = "3px solid yellow";
			card.style.boxShadow= "3px 3px 1px #888888";
		}
		
		this.createCardZoom(card, v);
		
		this.menuHandler.addMenu(card, v.menu);
		
		return card;
	},

	getCounter: function(v) {
		var html;
		if(typeof(v.counter0)!='undefined'&&typeof(v.counter1)!='undefined'&&typeof(v.counter2)!='undefined') {
			html = "";
			var dataFound = false;
			for(var i = 0 ; i < 3 ; i++) {
				if(html!="") {
					html += "/";	
				}
				if(v["counter"+i]!=null&&v["counter"+i]!="0"){
					html += v["counter"+i];
					dataFound = true;
				} else {
					html += "-";
				}
			}
			if(!dataFound) {
				html = "";
			}
		}
		else if(typeof(v.counter0)!='undefined') {
			html = v.counter0;
		} 
		else {
			html = "";
		}
		return html;
	}
	
});

});