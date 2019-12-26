// MIT License

// Copyright (c) 2019 JSAppSugar

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

var JSA = JSA || {};
var jsa = jsa || {};
var $engine = $engine || {
	f_redefine : function(func){
		var s = func.toString();
		s = s.replace(/\$super[ ]*\(/g,"this.\$super\(\"\$init\"\)\(");
		s = s.replace(/(\$super)[ ]*\.[ ]*([0-9a-zA-Z\$_]+)[ ]*\(/g,"this\.$1(\"$2\")\(");
		return eval("(function(){return("+s+");})();");
	}
};
JSA.$global = this;

(function(engine){
	"use strict";

	jsa.Object = function(){};
	JSA["$"+engine.lang] = true;

	jsa.Object.prototype.weakObject = engine.weakObject;
	jsa.Object.prototype.isWeak = engine.isWeak;
	jsa.Object.prototype.self = engine.self;

	jsa.Object.prototype.watch = function(prop,handler){
		var propDesc = Object.getOwnPropertyDescriptor(this, prop);
		if (propDesc && propDesc.get) return;
		var target = this;
		var oldValue = target[prop];
		var newValue = oldValue;
		var getter = function(){
			return newValue;
		};
		var setter = function(value){
			oldValue = newValue;
			newValue = value;
			handler.call(target, prop, oldValue, newValue);
		}
		if (delete target[prop]) {
			Object.defineProperty(target, prop, {
        get: getter,
        set: setter,
        enumerable: true,
        configurable: true
      });
		}
	};
	jsa.Object.prototype.unwatch = function(prop){
		var value = this[prop];
		delete this[prop];
		this[prop] = value;
	};


	var initializing = false;

	var f_$constructor = function(){
		if(!initializing && this.$init){
			if(arguments.length>0 && arguments[0] && arguments[0]["$native"]){
				this.$this = arguments[0]["$native"];
			}else{
				var args = undefined;
				if(arguments.length>0 && arguments[0]){
					args = arguments[0]["$arguments"];
				}
				this.$init.apply(this, args?args:arguments);
			}
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

	JSA.$interface = function(className,define){
		define["$interface"] = true;
		JSA.$class(className,define);
	}

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
			let isInterface = false;
			if(define["$interface"]){
				isInterface = true;
			}
			if(define["$implementation"] || isInterface){
				if(isInterface){
					JSAClass.$impl = "$interface";
					JSAClass.prototype.$init = engine.$init(undefined);
				}else{
					JSAClass.$impl = define["$implementation"]['$'+engine.lang];
					JSAClass.prototype.$init = engine.$init(define["$init"]?define["$init"]['$'+engine.lang]:undefined);
				}
				for(var key in define){
					if(key.charAt(0)==='$') continue;
					if(define[key]["$main"]){
						JSAClass.prototype[key] = engine.$function(define[key]["$"+engine.lang],true);
					}
					else{
						JSAClass.prototype[key] = engine.$function(define[key]["$"+engine.lang],false);
					}
				}
				JSAClass.fromNative = function(obj){
					return new JSAClass({"$native":obj});
				}
				if(!isInterface && define.$static){
					var staticDefine = define.$static;
					for(var key in staticDefine){
						if(key.charAt(0)=== '$'){
							JSAClass[key] = engine.$staticInitFunction(staticDefine[key]["$"+engine.lang]);
						}else{
							JSAClass[key] = engine.$staticFunction(staticDefine[key]["$"+engine.lang]);
						}
					}
				}
			}else{
				for(var key in define){
					if(key.charAt(0)==='$' && key !== '$init')
						continue;
					if(typeof define[key] == "function" && /\$super/.test(define[key])){
						JSAClass.prototype[key] =(
							function(defineFunction){
								if(engine.f_redefine) defineFunction = engine.f_redefine(defineFunction);
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
				if(define.$static && typeof define.$static === "object"){
					var defineStatic = define.$static;
					for(var key in defineStatic){
						JSAClass[key] = defineStatic[key];
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
			o = new cls({"$arguments":args});
		}
		return o;
	};
	JSA.$import = engine.$import;
	JSA.$classStaticVariable = function(className,variable){
		var cls = f_findClass(className);
		if(cls && cls[variable]){
			return cls[variable];
		}
		return null;
	};
	JSA.$classFunction = function(className,methodName,args){
		var cls = f_findClass(className);
		if(cls && cls[methodName]){
			return cls[methodName].apply(cls,args);
		}
		return null;
	};
	JSA.$class("jsa.NativeObject",{
		$init : function(self){
			this.$this = self;
		},
		invoke : engine.invoke,
		$static : {
			fromNative : function(self){
				return new jsa.NativeObject(self);
			}
		}
	});
}($engine));

$class = JSA.$class;
$interface = JSA.$interface;
$import = JSA.$import;
$newClass = JSA.$newClass;
$classFunction = JSA.$classFunction;
$classStaticVariable = JSA.$classStaticVariable;
delete $engine;
