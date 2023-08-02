package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;

import java.util.*;

/**
 * kd tree, k=3, rgb+
 */
public class ColorTree {
    public Node root;

    public ColorTree(NonNullList<ColoredBlock> blocks) {
        root = createNode(blocks, 0);
    }

    public ColoredBlock getBlock(ColorRGB value) {
        if (root == null)
            return null;
        // Find the closest leaf node
        Node prev = null;
        Node node = root;
        while (node != null) {
            if (compareTo(node.depth, value, node.block) <= 0) {
                // Lesser
                prev = node;
                node = node.lesser;
            } else {
                // Greater
                prev = node;
                node = node.greater;
            }
        }
        // Go up the tree, looking for better solutions
        node = prev;
        Node result = node;
        Set<Node> examined = new HashSet<>();
        while (node != null) {
            // Search node
            result = searchNode(value, node, result, examined);
            node = node.parent;
        }
        assert result != null;
        return result.block;
    }

    private static Node searchNode(ColorRGB value, Node node, Node results, Set<Node> examined) {
        examined.add(node);
        // Search node
        if (Float.compare(node.block.rgbSq(value), results.block.rgbSq(value)) < 0) {
            results = node;
        }
        int axis = node.depth % 3;
        float lastDistance = Mth.sqrt(results.block.disSq(value));
        Node lesser = node.lesser;
        Node greater = node.greater;
        // Search children branches, if axis aligned distance is less than
        // current distance
        float nodePoint;
        float valuePlusDistance;
        if (lesser != null && !examined.contains(lesser)) {
            examined.add(lesser);
            if (axis == 0) {
                nodePoint = node.block.r;
                valuePlusDistance = value.r - lastDistance;
            } else if (axis == 1) {
                nodePoint = node.block.g;
                valuePlusDistance = value.g - lastDistance;
            } else {
                nodePoint = node.block.b;
                valuePlusDistance = value.b - lastDistance;
            }
            // Continue down child branch
            if ((valuePlusDistance <= nodePoint))
                results = searchNode(value, lesser, results, examined);
        }
        if (greater != null && !examined.contains(greater)) {
            examined.add(greater);
            if (axis == 0) {
                nodePoint = node.block.r;
                valuePlusDistance = value.r + lastDistance;
            } else if (axis == 1) {
                nodePoint = node.block.g;
                valuePlusDistance = value.g + lastDistance;
            } else {
                nodePoint = node.block.b;
                valuePlusDistance = value.b + lastDistance;
            }
            // Continue down child branch
            if ((valuePlusDistance >= nodePoint))
                results = searchNode(value, greater, results, examined);
        }
        return results;
    }

    public static int compareTo(int depth, ColorRGB b1, ColoredBlock b2) {
        switch (depth % 3) {
            case 0 -> {
                return Float.compare(b1.r, b2.r);
            }
            case 1 -> {
                return Float.compare(b1.g, b2.g);
            }
            case 2 -> {
                return Float.compare(b1.b, b2.b);
            }
        }
        return 0;
    }

    public static int compareTo(int depth, ColoredBlock b1, ColoredBlock b2) {
        return compareTo(depth, new ColorRGB(b1.r, b1.g, b1.b), b2);
    }

    private static Node createNode(List<ColoredBlock> list, int depth) {
        if (list == null || list.isEmpty())
            return null;
        int axis = depth % 3;
        if (axis == 0)
            list.sort((a, b) -> Float.compare(a.r, b.r));
        else if (axis == 1)
            list.sort((a, b) -> Float.compare(a.g, b.g));
        else
            list.sort((a, b) -> Float.compare(a.b, b.b));

        Node node = null;
        List<ColoredBlock> less = new ArrayList<>(list.size());
        List<ColoredBlock> more = new ArrayList<>(list.size());
        if (!list.isEmpty()) {
            int medianIndex = list.size() / 2;
            node = new Node(list.get(medianIndex), depth);
            // Process list to see where each non-median point lies
            for (int i = 0; i < list.size(); i++) {
                if (i == medianIndex)
                    continue;
                ColoredBlock p = list.get(i);
                // Cannot assume points before the median are less since they could be equal
                if (compareTo(depth, p, node.block) <= 0) {
                    less.add(p);
                } else {
                    more.add(p);
                }
            }

            if ((medianIndex - 1 >= 0) && !less.isEmpty()) {
                node.lesser = createNode(less, depth + 1);
                node.lesser.parent = node;
            }

            if ((medianIndex <= list.size() - 1) && !more.isEmpty()) {
                node.greater = createNode(more, depth + 1);
                node.greater.parent = node;
            }
        }

        return node;
    }

    public static class Node {
        private final ColoredBlock block;
        private final int depth;
        private Node parent = null;
        private Node lesser = null;
        private Node greater = null;

        public Node(ColoredBlock block, int depth) {
            this.block = block;
            this.depth = depth;
        }
    }
}
