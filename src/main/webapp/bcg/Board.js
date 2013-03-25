define(
		[ "dojo/_base/declare", "./ServerCommunication", "./RemoteMsgHandlers", "./RemoteMsgSender" ], 
		function(declare, ServerCommunication, RemoteMsgHandlers, RemoteMsgSender) {

	return declare(null, {
		
		remoteMsgSender: null,
		remoteMsgHandlers: null,
		serverCommunication: null,
		
		constructor: function() {
			this.remoteMsgSender = new RemoteMsgSender();
			this.remoteMsgHandlers = new RemoteMsgHandlers(this.remoteMsgSender);
			this.serverCommunication = new ServerCommunication(this.remoteMsgHandlers, this.remoteMsgSender);			
		},
		
		create: function() {
			this.serverCommunication.init();
		}
	
	});

});