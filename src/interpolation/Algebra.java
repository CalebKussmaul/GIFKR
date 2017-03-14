package interpolation;

import javax.script.*;

public class Algebra {

	private ScriptEngine engine;
	private String function;
	
	public Algebra(String function) {
		
		engine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.function = function;
	}
	
	public float evalF(float x) {
		try {
			Object result = engine.eval("var x ="+x+";"+function);
			
			if (result instanceof Double)
				return ((Double) result).floatValue();
			if (result instanceof Integer)
				return ((Integer) result).floatValue();
			
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return 0f;
	}
	
	public String getFunction() {
		return function;
	}
	
	public boolean setFunction(String s) {
		s = s.toLowerCase();
		s = s.replace(" ", "");
		s = s.replace("max", "MAX");
		s = s.replace("exp", "EXP");
		
		System.out.println(s);
		
		s = s.replaceAll("(x|\\d+|\\d*\\.?\\d+)", "($1)"); //x -> (x)
		
		System.out.println(s);
		
		s = s.replaceAll("(\\d+)\\(", "$1*("); //5(
		s = s.replaceAll("\\)(\\d+)", ")*$1"); //)5
		
		System.out.println(s);
		
//		s = s.replaceAll("(x|\\d+)\\(", "$1*("); // x(...
//		s = s.replaceAll("\\)(x|\\d+)", ")*$1"); // ...)x
		s = s.replaceAll("(\\d+|\\))([a-z]+|\\()", "$1*$2"); // 5sin(...
		s = s.replaceAll("(\\(.*\\)|\\d+|[a-z]+)\\^(\\(.*\\)|\\d+|[a-z]+)", "pow($1, $2)"); // 5^(x)
		
		System.out.println(s);
		
		s = s.replace("pi", "(Math.PI)");
		s = s.replace("abs(", "Math.abs(");
		s = s.replace("acos(", "Math.acos(");
		s = s.replace("asin(", "Math.asin(");
		s = s.replace("atan(", "Math.atan(");
		s = s.replace("ceil(", "Math.ceil(");
		s = s.replace("cos(", "Math.cos(");
		s = s.replace("EXP(", "Math.exp(");
		s = s.replace("floor(", "Math.floor(");
		s = s.replace("log(", "Math.log(");
		s = s.replace("MAX(", "Math.max(");
		s = s.replace("min(", "Math.min(");
		s = s.replace("pow(", "Math.pow(");
		s = s.replace("random(", "Math.random(");
		s = s.replace("rand(", "Math.random(");
		s = s.replace("round(", "Math.round(");
		s = s.replace("sin(", "Math.sin(");
		s = s.replace("sqrt(", "Math.sqrt(");
		s = s.replace("tan(", "Math.tan(");
		
		
		System.out.println();
		try {
			if (engine.eval("var x = 1;"+s) instanceof Number) {
				function = s;
				return true;
			}
		} catch (ScriptException e) {}
		return false;
	}
}
