
$import(
	"test.jsa.NativeObject"
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
			return this.staticA+a;
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
			var r = this.obj.testString("s");
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
			var r = this.obj.testMap({a:1,b:"1",o:this.obj});
			a = r.a;
			b = r.b;
			var o = r.o;
			if((typeof a)=="number" && (typeof b) == "string" && a === 1 && b === "1"){
				return {a:1,b:"1",o:o};
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
		var nativeObj = test.jsa.NativeObject.fromNative(obj);
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
	}
});
