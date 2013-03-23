define(
		[ "dojo/_base/declare", "./ServerCommunication", "./RemoteMsgHandlers", "./RemoteMsgSender" ], function(declare, ServerCommunication, RemoteMsgHandlers, RemoteMsgSender) {

	return declare(null, {
		
		constructor: function() {
			var remoteMsgSender = new RemoteMsgSender();
			var remoteMsgHandlers = new RemoteMsgHandlers(remoteMsgSender);
			var serverCommunication = new ServerCommunication(remoteMsgHandlers, remoteMsgSender);
			serverCommunication.init();
		}
	
	});

});