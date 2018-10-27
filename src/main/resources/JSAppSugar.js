
var JSA = JSA || {};
var jsa = jsa || {};
JSA.$global = this;

(function(){
	"use strict";

	jsa.Object = function(){};

	var initializing = false;

	var f_$constructor = function(){
		if(!initializing && this.$init){
			this.$init.apply(this, arguments);
		}
	};

	var f_$super = function(name){
		var func = this.$SuperClass[name];
		var $this = this;
		return function(){
			return func.apply($this,arguments);
		};
	};

	var f_findClass = function(className){
		var packages = className.split('.');
		var l = packages.length-1;
		var currentPackage = JSA.$global;
		var i;
		for(i=0;i<l;i++){
			currentPackage = currentPackage[packages[i]];
			if(!currentPackage){
				return undefined;
			}
		}
		return currentPackage[packages[l]];
	}

	var f_applyClass=function(className,newClass){
		var packages = className.split('.');
		var l = packages.length-1;
		var currentPackage = JSA.$global;
		var i;
		for(i=0;i<l;i++){
			if(!currentPackage[packages[i]]){
				currentPackage[packages[i]] = {};
			}
			currentPackage = currentPackage[packages[i]];
		}
		currentPackage[packages[l]] = newClass;
	};

	JSA.$class = function(className,define){
		if (className != null && typeof className !== 'string') {
			throw new Error("[$class] Invalid class name '" + className + "' specified, must be a non-empty string");
		}
		var JSAClass = f_findClass(className);
		if(!JSAClass){
			JSAClass = function(){
				f_$constructor.apply(this,arguments);
			}

			var SuperClass = jsa.Object;
			if(define.$extends){
				if(typeof define.$extends !== 'string'){
					throw new Error("[$class] Invalid $extends class name '" + define.$extends + "' specified, must be a non-empty string");
				}
				SuperClass = f_findClass(define.$extends);
				if(!SuperClass){
					throw new Error("[$class] Invalid $extends class name '" + define.$extends + "' specified, the given class must be defined by $class");
				}
			}
			initializing = true;
			JSAClass.prototype = new SuperClass();
			initializing = false;
			JSAClass.prototype.constructor = JSAClass;
			JSAClass.$name = className;

			JSAClass.prototype.$super = f_$super;

			var SuperClassProto = SuperClass.prototype;
			if(define["$implementation"]){

			}else{
				for(var key in define){
					if(key.charAt(0)==='$' && key !== '$init')
						continue;
					if(typeof define[key] == "function" && typeof SuperClassProto[key] == "function"){
						JSAClass.prototype[key] =(
							function(defineFunction){
								return function(){
									var t = this.$SuperClass;
									this.$SuperClass = SuperClassProto;
									var result = defineFunction.apply(this,arguments);
									this.$SuperClass = t;
									return result;
								}
							}
							)(define[key]);
					}else{
						JSAClass.prototype[key] = define[key];
					}
				}
			}

			f_applyClass(className,JSAClass);
		}
	};
	JSA.$newClass = function(className,args){
		var cls = f_findClass(className);
		var o = undefined;
		if(cls){
			o = new cls();
			o.$init.apply(o,args);
		}
		return o;
	};
}());

$class = JSA.$class;
$newClass = JSA.$newClass;
