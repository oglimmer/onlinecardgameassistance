define(["dojox/socket", "dojo/io-query", "dojo/_base/declare", "dojo/dom" ],
		function(Socket, ioQuery, declare, dom) {

			return declare(null, {

				remoteMsgSender : null,
				remoteMsgHandlers : null,
				queryObject : null,
				socket : null,

				constructor : function(remoteMsgHandlers, remoteMsgSender) {
					this.remoteMsgHandlers = remoteMsgHandlers;
					this.remoteMsgSender = remoteMsgSender;
				},

				init : function() {
					var wsUrl;
					if(document.URL.indexOf('http://localhost:8080')==0) {
						wsUrl="ws://localhost:8082";
					} else {
						wsUrl="wss://swlcg-ws.oglimmer.de";
					}
					var self = this;
					this.queryObject = ioQuery.queryToObject(document.URL
							.substring(document.URL.indexOf("?") + 1,
									document.URL.length));
					
					var overlayTextNode = dom.byId("overlayText");
					overlayTextNode.innerHTML = overlayTextNode.innerHTML + this.queryObject.name;
					dom.byId("overlay").style.display = "block";
					
					this.socket = Socket({
						url : wsUrl,
						headers : {
							"Accept" : "application/json",
							"Content-Type" : "application/json"
						}
					});

					this.remoteMsgSender.socket = this.socket;
					this.remoteMsgSender.queryObject = this.queryObject;

					self.socket.on("open", function(e) {						
						self.socket.on("message", self.onMessage.bind(self));
						self.socket.on("error", self.onError.bind(self));
						self.socket.on("close", self.onClose.bind(self));
						self.remoteMsgSender.sendPreInitMsg();
						setTimeout(self.remoteMsgSender.sendPing.bind(self.remoteMsgSender), 1000*60);
					});

					require([ 'dojo/_base/unload' ], function(baseUnload) {
						baseUnload.addOnUnload(self.onUnload.bind(self));
					});
				},

				onMessage : function(e) {
					var message = e.data;
					var json = JSON.parse(message);
					console.log(json);
					for(var i = 0 ; i < json.length ; i++) {
						var msg = json[i];
						for ( var messageName in msg) {
							var parameters = msg[messageName];
							this.remoteMsgHandlers[messageName + "Handler"](parameters);
						}
					}
				},

				onError : function(e) {
					dom.byId("overlay").style.visibility = "";
					dom.byId("overlayText").innerHTML = "error";
					console.log(e);
				},

				onClose : function(e) {
					console.log("WS con closed");
				},

				onUnload : function() {
					console.log("unload");
					this.socket.close();
				}

			});

		});