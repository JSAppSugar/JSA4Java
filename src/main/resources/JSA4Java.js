
var $engine = $engine || {};



(function(){
	"use strict";

	$engine.lang = "java";

	$engine.$init = function(){
		return (function(){
			this.$this = $context.newClass(this.constructor.$impl,Array.prototype.slice.call(arguments));
		});
	};

	$engine.$function = function(define){
		var method = define;
		return (function(){
			return $context.invokeMethod(this.$this,method,Array.prototype.slice.call(arguments));;
		});
	}

	$engine.$import = function(classes){
		for(var i in classes){
			$context.importJSClass(classes[i]);
		}
	}

}());


