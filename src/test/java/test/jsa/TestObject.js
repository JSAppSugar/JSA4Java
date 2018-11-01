
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
	testInit:function(s,i){
		var obj = new test.jsa.NativeObject(s,i);
		return obj.getS()+obj.getI();
	}
});
