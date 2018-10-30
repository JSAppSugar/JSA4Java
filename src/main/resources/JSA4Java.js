
var $engine = $engine || {};



(function(){
	"use strict";

	$engine.lang = "java";

	$engine.$init = function(){
		this.$this = $context.newClass(this.constructor.$impl,Array.prototype.slice.call(arguments));
	};

	$engine.$function = function(define){
		return (function(){
		});
	}

	$engine.$import = function(classes){
		for(var i in classes){
			$context.importJSClass(classes[i]);
		}
	}

}());


