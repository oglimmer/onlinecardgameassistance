define([ "dojo/_base/declare", "dojo/dom", "dojo/dom-construct", "dojo/_base/array", "dojo/on", "dojo/query" ],
		function(declare, dom, domConstruct, arrayUtil, on, query) {

	return declare(null, {

		remoteMsgSender : null,
		imageBasePath : null,

		constructor : function(remoteMsgSender) {
			this.remoteMsgSender = remoteMsgSender;
		},
		
		_categoryChanged: function(v) {
			var curSel = searchCategory.value;
			arrayUtil.forEach(v.searchCategories, function(item, index) {
				if(item.name == curSel) {
					if(item.type == "TEXT") {
						dom.byId("searchText").style.display = "";
						dom.byId("searchValues").style.display = "none";
					} else {
						dom.byId("searchText").style.display = "none";
						var searchValues = dom.byId("searchValues");
						searchValues.style.display = "";
						searchValues.innerHTML = "";
						arrayUtil.forEach(item.values, function(item, index) {
							domConstruct.create("option", {
								innerHTML : item
							}, searchValues);
						});
					}
				}
			});
		},
		
		_searchButtonClicked: function(v) {
			var value;
			
			if(dom.byId("searchText").style.display == "") {
				value = dom.byId("searchText").value;
			}else {
				value = dom.byId("searchValues").value;
			}
			var data = {
					deckId:v.deckId,
					category:searchCategory.value,
					value:value
			};
			this.remoteMsgSender.search(data);
		},
		
		_closeButtonClicked: function(v) {
			dom.byId("searchDialog").style.display = "none";
		},
		
		
		_doButtonClicked: function(v) {
			var items = [];
			query("img", dom.byId("searchResult")).forEach(function(node) {
				if(node.style.border != "") {
					items.push(node.id.substring(9));
					domConstruct.destroy(node);
				}
			});					
			var operation = dom.byId("operation");
			var data = {
					deckId:v.deckId,
					operation:operation.value,
					items: items
			};
			
			this.remoteMsgSender.searchOperateOnResult(data);
		},
		
		showDialog: function(v) {
			var searchCategory = dom.byId("searchCategory");
			var searchDialog = dom.byId("searchDialog");
			
			dom.byId("searchText").style.display = "";
			dom.byId("searchValues").style.display = "none";
			searchCategory.innerHTML="";
			dom.byId("searchResult").innerHTML="";
			
			arrayUtil.forEach(v.searchCategories, function(item, index) {
				domConstruct.create("option", {
					innerHTML : item.name
				}, searchCategory);
			});
			
			var operation = dom.byId("operation");
			operation.innerHTML = "";
			arrayUtil.forEach(v.targets, function(item, index) {
				domConstruct.create("option", {
					value : item[0],
					innerHTML : "Move to "+item[1]+""
				}, operation);
			});
			
			this._addHandler(v, "searchCategory", "change", this._categoryChanged.bind(this));

			this._addHandler(v, "searchButton", "click", this._searchButtonClicked.bind(this));
			
			this._addHandler(v, "closeButton", "click", this._closeButtonClicked.bind(this));
						
			this._addHandler(v, "doButton", "click", this._doButtonClicked.bind(this));
			
			searchDialog.style.display = "block";
			
		},
		
		_addHandler: function(v, nodeName, eventName, functionName) {
			var handleName = nodeName+eventName+"Handler";
			if(typeof(this[handleName]) !== 'undefined') {
				this[handleName].remove();
			}
			this[handleName] = on(dom.byId(nodeName), eventName, function(e) {
				functionName(v);
			});
		},
		
		addResult: function(v) {
			var self = this;
			var searchResult = dom.byId("searchResult");
			searchResult.innerHTML = "";
			arrayUtil.forEach(v, function(item, index) {
				var card = domConstruct.create("img", {
					src : self.imageBasePath+"/cards/"+item.imageUrl,
					id: "SEARCHIMG"+item.id,
					style: {
						margin : "3px"
					}
				}, searchResult);
				self._createCardZoom(card, item);
				on(card, "click", function(e) {
					if(card.style.border == "") {
						card.style.border = "2px solid yellow";
						card.style.margin = "1px";
					} else {
						card.style.border = "";
						card.style.margin = "3px";
					}
				});
			});
		},
		
		
		_createCardZoom: function(c, v) {
			function removeZoomCard() {
				var toDestroy = dom.byId("ZOOMCARD");
				if(toDestroy!==null) {
					domConstruct.destroy(toDestroy);
				}
			}		
			on(c, "mouseover", function(e) {			
				var imgNode = dom.byId("SEARCHIMG"+v.id);						
				if(imgNode!=null) {
					removeZoomCard();
					domConstruct.create("img", {				
						src : imgNode.src.replace(/cards/g, "cardsLarge"),
						id : "ZOOMCARD",
						style : {
							zIndex: 2,
							position : "absolute",
							top : "5px",
							right : "5px"
						}
					}, "tableArea");
				}
			});
			on(c, "mouseout", function(e) {
				removeZoomCard();
			});	
		},
		
	});
});
