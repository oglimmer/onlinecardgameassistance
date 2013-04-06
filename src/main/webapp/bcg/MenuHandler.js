define([ "dojo/_base/declare", "dojo/_base/array", "dijit/Menu", "dijit/MenuItem", "dijit/MenuSeparator", "dojo/_base/lang", "dojo/query", "dojo/dom", "dojo/NodeList-dom" ], 
		function(declare, arrayUtil, Menu, MenuItem, MenuSeparator, lang, query, dom) {

return declare(null, {

	menuToNodeMapping : {},
	remoteMsgSender: null,
	
	constructor : function(remoteMsgSender) {
		this.remoteMsgSender = remoteMsgSender;
	},
	
	createMenu: function(nodeId, entries) {
		var pMenu = new Menu({
	        targetNodeIds: [nodeId],	    
	        leftClickToOpen: true
	    });
		pMenu.attachedCardId = nodeId;
		this.menuToNodeMapping[nodeId] = pMenu;
		arrayUtil.forEach(entries, function(item, index) {
			if(item.type==0) {
				pMenu.addChild(new MenuSeparator());
			} else if(item.type==1) {
				pMenu.addChild(new MenuItem({label: item.label, disabled: true }));
			} else {				
			    pMenu.addChild(new MenuItem({
			        label: item.label,
			        onClick: function(){
			        	item.fnct(item.actionName, item.params);
			        }
			    }));
			}
		});
		
		pMenu.onOpen = function(v) {
			/* HACK!!! dijit menus have a z-index of 1000. specifiy the z-index in the constructor mix doesn't work */
		    query('.dijitPopup').forEach(function(node) {
		    	node.style.zIndex = 999999999;
		    });
		    /* HACK!!! sometimes menu items are still selected from the last action. so remove manually */
			arrayUtil.forEach(this.getChildren(), function(v) {
				if(typeof(v._setSelected)!=='undefined') {
					v._setSelected(false);
				}
			});
		};
		
		pMenu._openMyself = function(args) {
			var cardDiv = dom.byId(this.attachedCardId);
			if(!this.leftClickToOpen || !cardDiv.moved) {
				Menu.prototype._openMyself.apply(this, arguments);
			}
			cardDiv.moved = false;
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

	addMenu: function(cardDiv, menu) {
		if(typeof(menu) == "undefined") {
			return;
		}
		var self = this;
		var menuData = new Array();
		arrayUtil.forEach(menu, function(item, index) {
			// item looks like "label:actionName:[param]"
			var splits = item.split(":");			
			var name = splits[0];
			if(name.charAt(0)=="~") {
				// headline
				menuData.push({type: 1, label : name.substring(1)});
			} else if(name=="-") {
				// separator
				menuData.push({type: 0});
			} else {
				// menu entry with remote action
				if(typeof(splits[2]) != 'undefined') {
					cardDiv = lang.clone(cardDiv);
					lang.mixin(cardDiv, { "param": splits[2] });
				}
				menuData.push({
					type: 2,
					label : name,
					fnct : self.remoteMsgSender.send.bind(self.remoteMsgSender),
					actionName : splits[1],
					params: cardDiv
				});
			}
		});
		if(menuData.length>0) {
			this.createMenu(cardDiv.id, menuData);
		}
	}	
	
});

});