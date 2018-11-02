
var $engine = $engine || {};



(function(){
	"use strict";
	
	var typesMapJ2JS = {
		"java.lang.Short" : "intValue",
		"java.lang.Integer" : "intValue",
		"java.lang.Character" : "",
		"java.lang.Long" : "longValue",
		"java.lang.Boolean" : "booleanValue",
		"java.lang.Float" : "doubleValue",
		"java.lang.Double" : "doubleValue",
		"java.lang.Byte" : "intValue",
		"java.lang.String" : "",
	};

	$engine.lang = "java";

	$engine.$init = function(){
		return (function(){
			var args = arguments.length==1?[arguments[0]]:Array.apply(null,arguments);
			this.$this = $context.newClass(this.constructor.$impl,args);
		});
	};

	$engine.$function = function(define){
		var method = define;
		return (function(){
			var args = arguments.length==1?[arguments[0]]:Array.apply(null,arguments);
			var v = $context.invokeMethod(this.$this,method,args);
			if(v && v["getClass"]){
				var javaClass = v.getClass().getName();
				var toJS = typesMapJ2JS[javaClass];
				if(toJS != undefined){
					if(toJS == "") v = v+"";
					else v = v[toJS]();
				}
			}
			return v;
		});
	}

	$engine.$import = function(classes){
		for(var i in classes){
			$context.importJSClass(classes[i]);
		}
	}

}());


