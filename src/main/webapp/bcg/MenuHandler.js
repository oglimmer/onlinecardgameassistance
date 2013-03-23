define([ "dojo/_base/declare", "dojo/_base/array", "dijit/Menu", "dijit/MenuItem", "dojo/_base/lang" ], 
		function(declare, arrayUtil, Menu, MenuItem, lang) {

return declare(null, {

	menuToNodeMapping : {},
	remoteMsgSender: null,
	
	constructor : function(remoteMsgSender) {
		this.remoteMsgSender = remoteMsgSender;
	},
	
	createMenu: function(node, entries) {
		var pMenu = new Menu({
	        targetNodeIds: [node]
	    });
		this.menuToNodeMapping[node] = pMenu;
		arrayUtil.forEach(entries, function(item, index) {
		    pMenu.addChild(new MenuItem({
		        label: item.label,
		        onClick: function(){
		        	item.fnct(item.params);
		        }
		    }));
		});
	    pMenu.startup();				
	},
	
	isDefined: function(id) {
		return typeof(this.menuToNodeMapping[id]) !== 'undefined';
	},
	
	removeMenu: function(id) {
		if(this.isDefined(id)){
			this.menuToNodeMapping[id].destroy();
			delete this.menuToNodeMapping[id];
		}
	},

	addMenu: function(v, menu) {
		if(typeof(menu) == "undefined") {
			return;
		}
		var self = this;
		var menuData = new Array();
		arrayUtil.forEach(menu, function(item, index) {
			var splits = item.split(":");
			var name = splits[0];
			var action = "send"+splits[1].substr(0,1).toUpperCase()+splits[1].substr(1);
			var fnct = self.remoteMsgSender[action];
			if(typeof(fnct)=='undefined' || fnct==null) {
				console.log(action);
			}
			fnct = fnct.bind(self.remoteMsgSender);
			if(typeof(splits[2]) != 'undefined') {
				v = lang.clone(v);
				lang.mixin(v, { "param": splits[2] });
			}
			menuData.push({
				label : name,
				fnct : fnct,
				params : v
			});
		});
		if(menuData.length>0) {
			this.createMenu(v.id, menuData);
		}
	}	
	
});

});