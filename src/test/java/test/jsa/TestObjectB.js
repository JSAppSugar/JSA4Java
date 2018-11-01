
$import([
	"test.jsa.TestObject"
	]);

$class("test.jsa.TestObjectB",{
	$extends:"test.jsa.TestObject",
	b:"-",
	$init:function(a,b){
		$super(a);
		if(b){
			this.b = b;
		}
	},
	getB:function(){
		return $super.getA()+this.b;
	}
});
