
$class("test.jsa.NativeObject",{
	$implementation:{
		$java : "test.java.JavaObject",
		$oc : "TestOCObject",
	},
	$init:{
		$oc:[
			"initWithString:Int:",
			"initWithNSDictionary:"
		]
	},
	$static:{
		staticA:{
			$java : "staticA",
			$oc : "staticA"
		},
		$init:{
			$java : "initWithParam",
			$oc : "initWithParam:"
		}
	},
	getS:{
		$java : "getS",
		$oc : "getS"
	},
	getI:{
		$java : "getI",
		$oc : "getI"
	},
	testNull:{
		$java : "testNull",
		$oc : "testNull:"
	},
	testString:{
		$java : "testString",
		$oc : "testString:"
	},
	testInt:{
		$java : "testInt",
		$oc : "testInt:"
	},
	testBool:{
		$java : "testBool",
		$oc : "testBool:"
	},
	testMap:{
		$java : "testMap",
		$oc : "testMap:"
	},
	testArray:{
		$java : "testArray",
		$oc : "testArray:"
	},
	testObject:{
		$java : "testObject",
		$oc : "testObject:"
	},
	testFunction:{
		$java : "testFunction",
		$oc : "testFunction:"
	},
	testJSAObject:{
		$java : "testJSAObject",
		$oc : "testJSAObject:"
	},
	testJSAFunction:{
		$java : "testJSAFunction",
		$oc : "testJSAFunction:"
	},
	workInMain:{
		$setView:true,
		$java : "workInMain",
		$oc : "workInMain"
	}
});
