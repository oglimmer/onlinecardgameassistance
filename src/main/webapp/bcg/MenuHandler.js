define([ "dojo/_base/declare", "dojo/_base/array", "dijit/Menu", "dijit/MenuItem", "dijit/MenuSeparator", "dojo/_base/lang", "dojo/query", "dojo/NodeList-dom" ], 
		function(declare, arrayUtil, Menu, MenuItem, MenuSeparator, lang, query) {

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
			if(item.label=="-") {
				pMenu.addChild(new MenuSeparator());
			} else {				
				var disabled = false;
				if(item.label.charAt(0) == "~"){
					disabled = true;
					item.label = item.label.substring(1);
				}
			    pMenu.addChild(new MenuItem({
			        label: item.label,
			        disabled: disabled,
			        onClick: function(){
			        	item.fnct(item.params);
			        }
			    }));
			}
		});
		
		/* HACK!!! dijit menus have a z-index of 1000. specifiy the z-index in the constructor mix doesn't work */
		pMenu.onOpen = function(v) {
		    query('.dijitPopup').forEach(function(node) {
		    	node.style.zIndex = 999999999;
		    });
		};
		
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
			var fnct = null;			
			if(name.charAt(0)!="~" && name!="-") {
				var action = "send"+splits[1].substr(0,1).toUpperCase()+splits[1].substr(1);
				fnct = self.remoteMsgSender[action];
				if(typeof(fnct)=='undefined' || fnct==null) {
					console.log(action);
				}
				fnct = fnct.bind(self.remoteMsgSender);
				if(typeof(splits[2]) != 'undefined') {
					v = lang.clone(v);
					lang.mixin(v, { "param": splits[2] });
				}
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