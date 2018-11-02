
$import([
	"test.jsa.NativeObject"
	]);

$class("test.jsa.TestObject",{
	a:"-",
	$init:function(a){
		if(a){
			this.a = a;
		}
		this.obj = new test.jsa.NativeObject();
	},
	getA:function(){
		return this.a;
	},
	testNativeInit:function(s,i){
		var obj = new test.jsa.NativeObject(s,i+0);
		return obj.getS()+obj.getI();
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
		if((typeof b) == "boolean" && b === true){
			var r = this.obj.testBool(true);
			if((typeof r) == "boolean" && r === true){
				return true;
			}
		}
		return false;
	}
});
