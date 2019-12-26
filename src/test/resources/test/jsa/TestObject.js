"use strict";

$import(
	"test.jsa.NativeObject",
	"test.jsa.NativeInterface"
	);

$class("test.jsa.TestObject",{
	a:"-",
	$init:function(a){
		if(a){
			this.a = a;
		}
		this.obj = new test.jsa.NativeObject();
	},
	$static:{
		staticA:"a",
		staticGetA(a){
			return test.jsa.TestObject.staticA+a;
		}
	},
	getA:function(){
		return this.a;
	},
	getNativeObj:function(){
		return this.obj;
	},
	getTestFunc:function(){
		var f = function(v){
			if(this && this["getA"]){
				return this.getA();
			}else{
				return v;
			}
		}
		return f;
	},
	testNativeInit:function(s,i){
		var obj = new test.jsa.NativeObject(s,i+0);
		return obj.getS()+obj.getI();
	},
	testNativeStatic:function(){
		return test.jsa.NativeObject.staticA();
	},
	testNull:function(v){
		if(v == undefined){
			var r = this.obj.testNull(null);
			if(r == undefined){
				return null;
			}
		}
		return "null";
	},
	testString:function(s){
		if((typeof s) == "string"){
			var t1 = {
				v : "s"
			};
			var t2 = ""+t1.v;
			var r = this.obj.testString(t2);
			if((typeof r) == "string"){
				return "s";
			}
		}
		return null;
	},
	testInt:function(i){
		if((typeof i) == "number" && i === 1){
			var r = this.obj.testInt(1);
			if((typeof r) == "number" && r === 1){
				return 1;
			}
		}
		return null;
	},
	testBool:function(b){
		if(b == true){
			var r = this.obj.testBool(true);
			if((typeof r) == "boolean" && r === true){
				return true;
			}
		}
		return false;
	},
	testMap:function(m){
		var a = m.a
		var b = m.b
		if((typeof a)=="number" && (typeof b) == "string" && a === 1 && b === "1"){
			var r = this.obj.testMap({
				a:1,
				b:"1",
				o:this.obj,
				f:function(){return 1},
				s:this,
			});
			a = r.a;
			b = r.b;
			var o = r.o;
			if((typeof a)=="number" && (typeof b) == "string" && a === 1 && b === "1"){
				return {
					a:1,
					b:"1",
					o:o,
					f:function(){
						return "function";
					}
				};
			}
		}
		return null;
	},
	testArray:function(m){
		var a = m[0];
		var b = m[1];
		if((typeof a)=="number" && (typeof b) == "string" && a === 1 && b === "1"){
			var r = this.obj.testArray([1,"1"]);
			a = r[0];
			b = r[1];
			if((typeof a)=="number" && (typeof b) == "string" && a === 1 && b === "1"){
				return [1,"1"];
			}
		}
		return null;
	},
	testObject:function(o){
		var r = this.obj.testObject(o);
		return r;
	},
	testJSAObject:function(o){
		if(o.constructor.$name == "test.jsa.TestObject" && o.getA() == "a"){
			var r = this.obj.testJSAObject(o);
			if(r === o){
				return o;
			}
		}
		return null;
	},
	testJSAFunction:function(f){
		if((typeof f) == "function"){
			var r = this.obj.testJSAFunction(f);
			if(r === f){
				return f;
			}
		}
		return null;
	},
	testWeakNativeA:function(obj){
		var nativeObj = test.jsa.NativeInterface.fromNative(obj);
		this.weakObj = nativeObj.weakObject();
	},
	testWeakNativeB:function(){
		var nativeObj = this.weakObj.self();
		return nativeObj;
	},
	testNativeObject:function(){
		if(JSA.$oc){
			var nativeJSObj = $new("TestOCObject","init");
			var weakObj = nativeJSObj.weakObject();
			return weakObj.self();
		}else if(JSA.$java){
			var nativeJSObj = $new("test.java.JavaObject");
			var weakObj = nativeJSObj.weakObject();
			return weakObj.self();
		}
		return null;
	},
	testPerformance:function(max){
		let i = 0;
		let c = 0;
		for(i=0;i<max;i++){
			c++;
		}
		return c;
	},
	testStaticInit:function(param){
		let o = test.jsa.NativeObject.$init(param);
		let s = o.getS();
		if(s == "s"){
			return o;
		}
		return null;
	},
	testWorkInMain:function(){
		let o = new test.jsa.NativeObject();
		o.workInMain();
	}
});
