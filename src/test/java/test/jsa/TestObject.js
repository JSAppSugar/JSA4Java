
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
	nativeGetA:function(){
		var nativeObj = new test.jsa.NativeObject("a");
		return nativeObj.getA();
	}
});
