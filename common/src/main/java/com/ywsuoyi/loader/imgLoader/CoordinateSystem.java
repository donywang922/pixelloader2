package com.ywsuoyi.loader.imgLoader;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * 像素画坐标系统
 * 包含x、y、z三个坐标的计算公式
 */
public class CoordinateSystem {
    private final ExpressionUtil.CompiledExpression xFormula;
    private final ExpressionUtil.CompiledExpression yFormula;
    private final ExpressionUtil.CompiledExpression zFormula;

    public CoordinateSystem(String xExpr, String yExpr, String zExpr) {
        this.xFormula = ExpressionUtil.compile(xExpr);
        this.yFormula = ExpressionUtil.compile(yExpr);
        this.zFormula = ExpressionUtil.compile(zExpr);
    }

    /**
     * 计算像素(u,v)对应的世界坐标
     *
     * @param u 像素的横坐标
     * @param v 像素的纵坐标
     * @param w 宽
     * @param h 高
     * @param z 图层编号
     * @return 世界坐标[x, y, z]
     */
    public BlockPos getWorldCoords(int u, int v, int w, int h, int z) {
        Map<String, Double> vars = new HashMap<>(2);
        vars.put("u", (double) u);
        vars.put("v", (double) v);
        vars.put("w", (double) w);
        vars.put("h", (double) h);
        vars.put("z", (double) z);

        int a = (int) Math.round(xFormula.evaluate(vars));
        int b = (int) Math.round(yFormula.evaluate(vars));
        int c = (int) Math.round(zFormula.evaluate(vars));

        return new BlockPos(a, b, c);
    }
}
