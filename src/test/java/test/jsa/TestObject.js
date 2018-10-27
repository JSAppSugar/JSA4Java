
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
});
