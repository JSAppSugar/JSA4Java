
$import([
	"test.jsa.NativeObject"
	]);

$class("test.jsa.TestObject",{
	a:"-",
	$init:function(a){
		if(a){
			this.a = a;
		}
	},
	getA:function(){
		return this.a;
	},
	testNativeInit:function(s,i){
		var obj = new test.jsa.NativeObject(s,i+0);
		return obj.getS()+obj.getI();
	}
});
