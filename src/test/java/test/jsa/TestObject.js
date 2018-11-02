
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
	}
});
