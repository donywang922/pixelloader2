package com.ywsuoyi.loadingThreadUtil;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record CachedQuadData(List<CachedQuad> quads) {

    public record CachedQuad(BakedQuad quad, BlockPos pos) {
    }

    public static boolean isFaceOccluded(BlockPos pos, Direction direction, Set<BlockPos> blockPositions) {
        BlockPos neighborPos = pos.relative(direction);
        return blockPositions.contains(neighborPos);
    }

    public static List<CachedQuadData.CachedQuad> generateCachedQuads(List<Tuple<BlockPos, BlockState>> blocks, BlockRenderDispatcher blockRender) {
        List<CachedQuadData.CachedQuad> cachedQuads = new ArrayList<>();

        // 创建位置集合用于快速查找
        Set<BlockPos> blockPositions = new HashSet<>();
        for (Tuple<BlockPos, BlockState> tuple : blocks) {
            if (tuple.getB().is(Blocks.GLASS) || tuple.getB().isAir()) continue;
            blockPositions.add(tuple.getA());
        }

        RandomSource randomSource = RandomSource.create(42);

        for (Tuple<BlockPos, BlockState> tuple : blocks) {
            if (tuple.getB().is(Blocks.GLASS) || tuple.getB().isAir()) continue;
            BlockPos pos = tuple.getA();
            BlockState blockState = tuple.getB();
            BakedModel blockModel = blockRender.getBlockModel(blockState);

            // 检查每个方向的面
            for (Direction direction : Direction.values()) {
                // 如果这个面没有被遮挡，则添加到缓存中
                if (!isFaceOccluded(pos, direction, blockPositions)) {
                    List<BakedQuad> quads = blockModel.getQuads(blockState, direction, randomSource);
                    for (BakedQuad quad : quads) {
                        cachedQuads.add(new CachedQuadData.CachedQuad(quad, pos));
                    }
                }
            }

            // 也检查null方向的面（内部面）
            List<BakedQuad> nullQuads = blockModel.getQuads(blockState, null, randomSource);
            for (BakedQuad quad : nullQuads) {
                cachedQuads.add(new CachedQuadData.CachedQuad(quad, pos));
            }
        }

        return cachedQuads;
    }

    public static CachedQuadData build(List<Tuple<BlockPos, BlockState>> blocks, BlockRenderDispatcher blockRender) {
        return new CachedQuadData(generateCachedQuads(blocks, blockRender));
    }
}
