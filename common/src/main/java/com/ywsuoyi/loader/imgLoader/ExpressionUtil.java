package com.ywsuoyi.loader.imgLoader;

import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 像素画生成器坐标计算器
 * 用于计算每个像素(u,v)对应的方块世界坐标(x,y,z)
 */
public class ExpressionUtil {

    // 编译后的表达式缓存
    private static final Map<String, CompiledExpression> CACHE = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 100; // 像素画公式通常不多

    public static CompiledExpression compile(String expression) {
        String key = expression.replaceAll("\\s+", "");
        CompiledExpression compiled = CACHE.get(key);
        if (compiled == null) {
            if (CACHE.size() >= MAX_CACHE_SIZE) {
                CACHE.clear();
            }
            compiled = new CompiledExpression(key);
            CACHE.put(key, compiled);
        }
        return compiled;
    }

    public record CompiledExpression(String expression) {
        public double evaluate(Map<String, Double> variables) {
            return parseExpression(expression, 0, variables).value;
        }
    }

    private record ParseResult(double value, int nextIndex) {
    }

    private static ParseResult parseExpression(String expr, int index, Map<String, Double> vars) {
        ParseResult left = parseTerm(expr, index, vars);

        while (left.nextIndex < expr.length()) {
            char op = expr.charAt(left.nextIndex);
            if (op == '+' || op == '-') {
                ParseResult right = parseTerm(expr, left.nextIndex + 1, vars);
                double result = (op == '+') ? left.value + right.value : left.value - right.value;
                left = new ParseResult(result, right.nextIndex);
            } else {
                if (op == ')') {
                    break; // 这是有效的结束，让上层处理
                } else {
                    // 遇到未知操作符，报错
                    throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.invalidChar", String.valueOf(op)).getString());
                }
            }
        }

        return left;
    }

    private static ParseResult parseTerm(String expr, int index, Map<String, Double> vars) {
        ParseResult left = parsePower(expr, index, vars);

        while (left.nextIndex < expr.length()) {
            char op = expr.charAt(left.nextIndex);
            if (op == '*' || op == '/') {
                ParseResult right = parsePower(expr, left.nextIndex + 1, vars);
                double result = (op == '*') ? left.value * right.value : left.value / right.value;
                left = new ParseResult(result, right.nextIndex);
            } else {
                if (op == '+' || op == '-' || op == ')') {
                    break; // 这些是有效的结束，让上层处理
                } else {
                    // 遇到未知操作符，报错
                    throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.invalidChar", String.valueOf(op)).getString());
                }
            }
        }

        return left;
    }

    private static ParseResult parsePower(String expr, int index, Map<String, Double> vars) {
        ParseResult left = parseFactor(expr, index, vars);

        if (left.nextIndex < expr.length() && expr.charAt(left.nextIndex) == '^') {
            ParseResult right = parsePower(expr, left.nextIndex + 1, vars);
            double result = Math.pow(left.value, right.value);
            return new ParseResult(result, right.nextIndex);
        }

        return left;
    }

    private static ParseResult parseFactor(String expr, int index, Map<String, Double> vars) {
        if (index >= expr.length()) {
            throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.incomplete").getString());
        }

        char ch = expr.charAt(index);

        if (ch == '(') {
            ParseResult result = parseExpression(expr, index + 1, vars);
            if (result.nextIndex >= expr.length() || expr.charAt(result.nextIndex) != ')') {
                throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.missingRightParen").getString());
            }
            return new ParseResult(result.value, result.nextIndex + 1);
        }

        if (ch == '-') {
            ParseResult result = parseFactor(expr, index + 1, vars);
            return new ParseResult(-result.value, result.nextIndex);
        }

        if (ch == '+') {
            return parseFactor(expr, index + 1, vars);
        }

        if (Character.isLetter(ch)) {
            return parseFunction(expr, index, vars);
        }

        if (Character.isDigit(ch) || ch == '.') {
            return parseNumber(expr, index);
        }

        throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.invalidChar", String.valueOf(ch)).getString());
    }

    private static ParseResult parseNumber(String expr, int index) {
        int start = index;
        boolean hasDot = false;

        while (index < expr.length()) {
            char ch = expr.charAt(index);
            if (Character.isDigit(ch)) {
                index++;
            } else if (ch == '.' && !hasDot) {
                hasDot = true;
                index++;
            } else {
                break;
            }
        }

        double value = Double.parseDouble(expr.substring(start, index));
        return new ParseResult(value, index);
    }

    private static ParseResult parseFunction(String expr, int index, Map<String, Double> vars) {
        int start = index;
        while (index < expr.length() && (Character.isLetterOrDigit(expr.charAt(index)) || expr.charAt(index) == '_')) {
            index++;
        }

        String name = expr.substring(start, index);

        if (index < expr.length() && expr.charAt(index) == '(') {
            return parseFunctionCall(expr, name, index, vars);
        }

        if (vars != null && vars.containsKey(name)) {
            return new ParseResult(vars.get(name), index);
        }

        // 支持常量
        return switch (name) {
            case "PI" -> new ParseResult(Math.PI, index);
            case "E" -> new ParseResult(Math.E, index);
            default -> throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.unknownVariable", name).getString());
        };
    }

    private static ParseResult parseFunctionCall(String expr, String funcName, int index, Map<String, Double> vars) {
        index++; // 跳过 '('

        ParseResult arg = parseExpression(expr, index, vars);

        if (arg.nextIndex >= expr.length() || expr.charAt(arg.nextIndex) != ')') {
            throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.functionMissingRightParen").getString());
        }

        double result = applyFunction(funcName, arg.value);
        return new ParseResult(result, arg.nextIndex + 1);
    }

    private static double applyFunction(String name, double arg) {
        return switch (name.toLowerCase()) {
            case "sin" -> Math.sin(arg);
            case "cos" -> Math.cos(arg);
            case "tan" -> Math.tan(arg);
            case "asin" -> Math.asin(arg);
            case "acos" -> Math.acos(arg);
            case "atan" -> Math.atan(arg);
            case "sqrt" -> Math.sqrt(arg);
            case "log" -> Math.log(arg);
            case "log10" -> Math.log10(arg);
            case "exp" -> Math.exp(arg);
            case "abs" -> Math.abs(arg);
            case "floor" -> Math.floor(arg);
            case "ceil" -> Math.ceil(arg);
            case "round" -> Math.round(arg);
            default -> throw new IllegalArgumentException(Component.translatable("pixelLoader.expression.error.unknownFunction", name).getString());
        };
    }
}