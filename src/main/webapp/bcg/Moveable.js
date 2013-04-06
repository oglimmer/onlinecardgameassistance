define(["dojo/_base/declare", "dojo/dnd/Moveable"], function(declare, Moveable) {

return declare(Moveable, {
	
	node: null,
	nodeConfig:null,
	sendMoveCardMsg:null,
	
	constructor: function(node, nodeConfig, sendMoveCardMsg) {
		this.node = node;
		this.nodeConfig = nodeConfig;
		this.sendMoveCardMsg = sendMoveCardMsg;
	},
	
	onMoveStop : function() {
		if (parseInt(this.node.style.top,10) < 0) {
			this.node.style.top = "0px";
		}
		if (parseInt(this.node.style.left, 10) < 0) {
			this.node.style.left = "0px";
		}
		if (parseInt(this.node.style.top, 10) > this.node.parentNode.offsetHeight - this.node.offsetHeight) {
			this.node.style.top = (this.node.parentNode.offsetHeight - this.node.offsetHeight) + "px";
		}
		if (parseInt(this.node.style.left, 10) > this.node.parentNode.offsetWidth - this.node.offsetWidth) {
			this.node.style.left = (this.node.parentNode.offsetWidth - this.node.offsetWidth) + "px";
		}
		
		var xPos = parseInt(this.node.style.left, 10);
		var yPos = parseInt(this.node.style.top, 10);
		this.sendMoveCardMsg(this.nodeConfig,xPos,yPos);
		
		this.inherited(arguments);
	},
	
	onMouseDown : function(e) {
		// prevent "start move" when right-clicking 
		if(e.button == 2) {
			return;
		}
		this.inherited(arguments);
	},
	
	onFirstMove: function(mover, e) {
		this.node.moved = true;
	}
	
	
});	

});