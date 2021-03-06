package tech.iopi.jsa.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Neal
 *
 */
class ObjectAccessor {

	@SuppressWarnings("unchecked")
	public static <T> T constructor(Class<T> c,Object... args){
		Constructor<?> con = null;
		Class<?>[] argTypes = getArgTypes( args );
		Class<?>[] constructorTypes;
		int matchScore = NOT_MATCH,score;
		Constructor<?>[] constructors = c.getConstructors();
		
		for(Constructor<?> constructor:constructors){
			constructorTypes = constructor.getParameterTypes();
			boolean isVarArgs = constructor.isVarArgs();
			score = testMatch(argTypes,constructorTypes,isVarArgs);
			if(isVarArgs)score-=1;
			if(score != NOT_MATCH && score>matchScore){
				matchScore = score;
				con = constructor;
			}
		}
		if(con==null){
			throw new RuntimeException(new NoSuchMethodException("no public constructor : "+c.getName()+argumentTypesToString(argTypes)));
		}
		
		if(con.isVarArgs()&&args!=null){
			args = transVarArgs(con.getParameterTypes(),args);
		}else{
			args = convertArgs(con.getParameterTypes(), args);
		}
		try {
			return (T)con.newInstance( args );
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object method(Object obj,String methodName,Object... args){
		Object methodObject = obj;
		Class<?> objectClass = obj.getClass();
		if(obj instanceof Class){
			objectClass = (Class<?>)methodObject;
			methodObject = null;
		}
		Method m = getProperMethod(objectClass,methodName,methodObject==null,args);
		if(m.isVarArgs()&&args!=null) args = transVarArgs(m.getParameterTypes(),args);
		else if(args == null) args = new Object[]{null};
		else args = convertArgs(m.getParameterTypes(), args);
		try {
			return m.invoke(methodObject, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Object[] convertArgs(Class<?>[] varTypes,Object... args) {
		for(int i=0;i<varTypes.length;i++) {
			Class<?> needType = varTypes[i];
			if(needType.isPrimitive()) {
				needType = primitiveMap.get(needType);
			}
			if(Number.class.isAssignableFrom(needType)) {
				Number value = (Number)args[i];
				if(needType == Short.class) {
					args[i] = value.shortValue();
				}else if(needType == Integer.class) {
					args[i] = value.intValue();
				}else if(needType == Long.class) {
					args[i] = value.longValue();
				}else if(needType == Float.class) {
					args[i] = value.floatValue();
				}else if(needType == Double.class) {
					args[i] = value.doubleValue();
				}else if(needType == Byte.class) {
					args[i] = value.byteValue();
				}
			}else if(needType == Boolean.class && args[i] instanceof Number) {
				int value = ((Number)args[i]).intValue();
				args[i] = value!=0;
			}else if(needType == Character.class) {
				args[i] = args[i].toString().charAt(0);
			}
		}
		return args;
	}
	
	private static Object[] transVarArgs(Class<?>[] varTypes,Object... args){
		Object[] newArgs = new Object[varTypes.length];
		Object[] varArgs = null;
		int varArgIndex = 0;
		boolean notChecked = true;
		for(int i=0;i<args.length;i++){
			if((i+1==varTypes.length)){
				varArgs = (Object[]) Array.newInstance(varTypes[i].getComponentType(), args.length-varTypes.length+1);
			}else if( i< varTypes.length ){
				newArgs[i]= args[i];
			}
			if(varArgs!=null){
				if(notChecked && args[i]!=null && args[i].getClass().getComponentType()!=null){
					notChecked = false;
					varArgs = (Object[]) args[i];
					break;
				}
				varArgs[varArgIndex++]=args[i];
			}
		}
		newArgs[varTypes.length-1]=varArgs;
		return newArgs;
	}
	
	
	private final static int NOT_MATCH = 0;
	private final static int MATCH = 1000;
	private final static int MAY_MATCH = 2;
	private static Method getProperMethod(Class<?> c,String methodName,boolean isStatic,Object... args){
		Method m = null;
		Class<?>[] argTypes = getArgTypes( args );
		Method[] methods = MethodManager.getMethods( c, methodName, argTypes.length ,true);
		m = getProperMethod(methods,isStatic,false,argTypes);
		if(m==null){
			methods = MethodManager.getMethods( c, methodName, -1 ,true);
			m = getProperMethod(methods,isStatic,true,argTypes);
		}
		
		if(m==null){
			throw new RuntimeException(new NoSuchMethodException("no public "+isStaticToString(isStatic)+" method : "+c.getName() + "." + methodName + argumentTypesToString(argTypes)));
		}
		return m;
	}
	private static Method getProperMethod(Method[] methods,boolean isStatic,boolean isVarArgs,Class<?>[] argTypes){
		if(methods == null )return null;
		Method m = null;
		Class<?>[] methodTypes;
		int matchScore = NOT_MATCH,score,mod;
		for( Method method:methods ) {
			mod = method.getModifiers();
			if( !isStatic||isStatic&&Modifier.isStatic( mod ) ) {
				methodTypes = method.getParameterTypes();
				score = testMatch(argTypes,methodTypes,isVarArgs);
				if(score != NOT_MATCH && score>matchScore){//匹配
					matchScore = score;
					m = method;
				}
			}
		}
		return m;
	}
	private static int testMatch(Class<?>[] argTypes, Class<?>[] paramTypes,boolean isVarArgs){
		if(!isVarArgs && paramTypes.length!=argTypes.length)return NOT_MATCH;
		Class<?> arg, param=null;
		int total = MAY_MATCH, score;
		for( int i=0;i<argTypes.length;i++ ) {
			arg = argTypes[i];
			if(isVarArgs && (i+1)==paramTypes.length){
				if(arg.getComponentType()==null)param = paramTypes[i].getComponentType();
				else{
					score = testMatch(arg,paramTypes[i]);
					if(score==NOT_MATCH)return NOT_MATCH;
					return total + score;
				}
			}
			else if( i< paramTypes.length ){
				param = paramTypes[i];
			}
			score = testMatch(arg,param);
			if(score==NOT_MATCH) return NOT_MATCH;
			total+=score;
		}
		return total;
	}
	private static int testMatch(Class<?> argType,Class<?> paramType){
		if(argType==null){
			if(paramType.isPrimitive()) return NOT_MATCH;
			else return MAY_MATCH;
		}
		if( !paramType.isPrimitive() ) {
			if( argType.equals( paramType ) ) {
				return MATCH;
			} else if( paramType.isAssignableFrom( argType ) ) {
				return MAY_MATCH;
			} else if(Number.class.isAssignableFrom(paramType) && Number.class.isAssignableFrom(argType)) {
				return MAY_MATCH;
			}
			else {
				return NOT_MATCH;
			}
		} else {
			return primitiveEquals( argType, paramType );
		}
	}
	private final static Map<Class<?>, Class<?>> primitiveMap;
	static {
		Map<Class<?>,Class<?>> tmp = new HashMap<Class<?>, Class<?>>();
		tmp.put( short.class, Short.class );
		tmp.put( int.class, Integer.class );
		tmp.put( char.class, Character.class );
		tmp.put( long.class, Long.class );
		tmp.put( boolean.class, Boolean.class );
		tmp.put( float.class, Float.class );
		tmp.put( double.class, Double.class );
		tmp.put( byte.class, Byte.class );
		primitiveMap = Collections.unmodifiableMap( tmp );
	}
	private static int primitiveEquals( Class<?> wrapper, Class<?> prim ) {
		if( prim.equals( wrapper ) ) {
			return MATCH;
		}
		Class<?> primitiveClass = primitiveMap.get( prim );
		if( primitiveClass.equals( wrapper ) ) {
			return MAY_MATCH;
		}
		if(Number.class.isAssignableFrom(primitiveClass) && Number.class.isAssignableFrom(wrapper)) {
			return MAY_MATCH;
		}
		return NOT_MATCH;
	}
	
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[1];
	private static Class<?>[] getArgTypes(Object... args){
		if(args == null)return EMPTY_CLASS_ARRAY;
		int len = args.length;
		Class<?>[] types = new Class[ len ];
		for(int i=0;i<len;i++){
			if( args[i]!=null ) types[i] = args[i].getClass();
		}
		return types;
	}
	
	private static class MethodManager{
		private static Map<Class<?>,Map<String,Method[]>> publicClassMethodCache = new HashMap<Class<?>,Map<String,Method[]>>();
		private static Map<Class<?>,Map<String,Method[]>> otherClassMethodCache = new HashMap<Class<?>,Map<String,Method[]>>();
		
		public static Method[] getMethods( Class<?> c, String name, int argLen ,boolean isPublic){
			Map<String,Method[]> classMethods = getClassMethods(c,isPublic);
			return classMethods.get( getMethodKey( name, argLen) );
		}
		
		private static Map<String,Method[]> getClassMethods(Class<?> c,boolean isPublic){
			Map<String,Method[]> classMethods = null;
			
			Map<Class<?>,Map<String,Method[]>> needCache = publicClassMethodCache;
			if(!isPublic) needCache = otherClassMethodCache;
			
			synchronized(publicClassMethodCache){
				classMethods = needCache.get(c);
			}
			if(classMethods == null){
				synchronized(c){
					synchronized(publicClassMethodCache){
						classMethods = needCache.get(c);
					}
					if(classMethods == null){
						Map<String, List<Method>> mMap = new LinkedHashMap<String, List<Method>>();
						Method[] allMethods = null;
						if(isPublic) allMethods = c.getMethods();
						else allMethods = c.getDeclaredMethods();
						for( Method m:allMethods) {
							if(!isPublic && Modifier.isPublic(m.getModifiers())) continue;
							int typesLen = m.getParameterTypes().length;
							if(m.isVarArgs())typesLen = -1;
							String mKey = getMethodKey( m.getName(), typesLen);
							List<Method> methods = mMap.get( mKey );
							if( methods==null ) {
								methods = new ArrayList<Method>();
								mMap.put(mKey, methods);
							}
							methods.add(m);
						}
						classMethods = new HashMap<String,Method[]>();
						for( Map.Entry<String, List<Method>> en:mMap.entrySet() ) {
							classMethods.put( en.getKey(), en.getValue().toArray( new Method[]{} ) );
						}
						synchronized(publicClassMethodCache){
							needCache.put(c, classMethods);
						}
					}
				}
			}
			
			return classMethods;
		}
		private static String getMethodKey( String methodName, int argLen) {
			return argLen + methodName;
		}
	}
	
	private static String isStaticToString(boolean isStatic){
		if(isStatic) return " static";
		return "";
	}
	
	private static String argumentTypesToString(Class<?>[] argTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
		Class<?> c = argTypes[i];
		buf.append((c == null) ? "null" : c.getName());
            }
        }
        buf.append(")");
        return buf.toString();
    }
}
