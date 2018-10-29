
var $engine = $engine || {};



(function(){
	"use strict";

	$engine.lang = "java";

	$engine.$init = function(){
		
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


