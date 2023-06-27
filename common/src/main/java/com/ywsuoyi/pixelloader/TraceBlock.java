package com.ywsuoyi.pixelloader;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class TraceBlock extends Block {
    public static final IntegerProperty point = IntegerProperty.create("point", 0, 26);

    public static final int[] round = new int[]{1, 0, 1, 1, 0, 1, -1, 1, -1, 0, -1, -1, 0, -1, 1, -1};

    public static final int[] neb = new int[78];

    public TraceBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(point, 0));
    }

    @Override
    public float getShadeBrightness(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return 1.0f;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(point);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        for (int i = 0; i < 26; i++) {
            BlockPos tmpPos = blockPos.offset(neb[i * 3], neb[i * 3 + 1], neb[i * 3 + 2]);
            BlockState nb = level.getBlockState(tmpPos);
            if (nb.is(this)) {
                if (nb.getValue(point) == 0) {
                    level.setBlock(tmpPos, blockState.setValue(point, toID(-neb[i * 3], -neb[i * 3 + 1], -neb[i * 3 + 2])), 3);
                    break;
                }
            }
        }
        if (livingEntity != null && !livingEntity.isShiftKeyDown()) tryPoint(level, blockPos, blockState);
        updateCenter(level, blockPos);
    }

    public void tryPoint(LevelAccessor level, BlockPos blockPos, BlockState blockState) {
        for (int i = 0; i < 26; i++) {
            BlockPos tmpPos = blockPos.offset(neb[i * 3], neb[i * 3 + 1], neb[i * 3 + 2]);
            BlockState nb = level.getBlockState(tmpPos);
            if (nb.is(this) && !tmpPos.offset(fromID(nb.getValue(point))).equals(blockPos)) {
                boolean flagNpt = true;
                for (int j = 0; j < 26; j++) {
                    BlockPos tmpPos2 = tmpPos.offset(neb[j * 3], neb[j * 3 + 1], neb[j * 3 + 2]);
                    if (tmpPos2.equals(blockPos)) break;
                    BlockState nb2 = level.getBlockState(tmpPos2);
                    if (nb2.is(this) && tmpPos2.offset(fromID(nb2.getValue(point))).equals(tmpPos)) {
                        flagNpt = false;
                        break;
                    }
                }
                if (flagNpt) {
                    level.setBlock(blockPos, blockState.setValue(point, toID(neb[i * 3], neb[i * 3 + 1], neb[i * 3 + 2])), 3);
                    return;
                }
            }
        }
        level.setBlock(blockPos, blockState.setValue(point, 0), 3);
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        super.destroy(levelAccessor, blockPos, blockState);
        for (int i = 0; i < 26; i++) {
            BlockPos tmpPos = blockPos.offset(neb[i * 3], neb[i * 3 + 1], neb[i * 3 + 2]);
            BlockState nb = levelAccessor.getBlockState(tmpPos);
            if (nb.is(this) && tmpPos.offset(fromID(nb.getValue(point))).equals(blockPos)) {
                tryPoint(levelAccessor, tmpPos, nb);
                break;
            }
        }
        updateCenter(levelAccessor, blockPos);
    }

    private void updateCenter(LevelAccessor levelAccessor, BlockPos blockPos) {
        for (int i = 0; i < 26; i++) {
            BlockPos tmpPos = blockPos.offset(neb[i * 3], neb[i * 3 + 1], neb[i * 3 + 2]);
            BlockState nb = levelAccessor.getBlockState(tmpPos);
            if (nb.is(PixelLoader.traceCenterBlock)) {
                PixelLoader.traceCenterBlock.updatePoint(levelAccessor, tmpPos, nb);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!player.getItemInHand(interactionHand).isEmpty()) return InteractionResult.PASS;
        int pn = blockState.getValue(point);
        if (pn == 0) {
            level.setBlock(blockPos, blockState.setValue(point, 1), 3);
            return InteractionResult.SUCCESS;
        }
        HashMap<String, Integer> pos = new HashMap<>();
        BlockPos tp = fromID(pn);
        pos.put("x", tp.getX());
        pos.put("y", tp.getY());
        pos.put("z", tp.getZ());
        int rotd = 0;
        String x = "x", y = "y";
        switch (blockHitResult.getDirection()) {
            case UP -> {
                rotd = 1;
                x = "x";
                y = "z";
            }
            case DOWN -> {
                rotd = -1;
                x = "x";
                y = "z";
            }
            case NORTH -> {
                rotd = 1;
                x = "x";
                y = "y";
            }
            case SOUTH -> {
                rotd = -1;
                x = "x";
                y = "y";
            }
            case WEST -> {
                rotd = -1;
                x = "z";
                y = "y";
            }
            case EAST -> {
                rotd = 1;
                x = "z";
                y = "y";
            }
        }
        if (pos.get(x) == 0 && pos.get(y) == 0) return InteractionResult.SUCCESS;
        for (int i = 0; i < 8; i++) {
            if (round[i * 2] == pos.get(x) && round[i * 2 + 1] == pos.get(y)) {
                i = i + (player.isShiftKeyDown() ? -1 : 1) * rotd;
                if (i > 7) i = 0;
                if (i < 0) i = 7;
                pos.put(x, round[i * 2]);
                pos.put(y, round[i * 2 + 1]);
                break;
            }
        }

        level.setBlock(blockPos, blockState.setValue(point, toID(pos.get("x"), pos.get("y"), pos.get("z"))), 3);
        return InteractionResult.SUCCESS;
    }

    public static BlockPos fromID(int id) {
        for (PDirection p : PDirection.values()) {
            if (id == p.id) {
                return new BlockPos(p.Xoffset, p.Yoffset, p.Zoffset);
            }
        }
        return BlockPos.ZERO;
    }

    public static int toID(int x, int y, int z) {
        for (PDirection p : PDirection.values()) {
            if (x == p.Xoffset && y == p.Yoffset && z == p.Zoffset) {
                return p.id;
            }
        }
        return 0;
    }

    public enum PDirection {
        emp(0, 0, 0, 0),//none
        x(1, 0, 0, 1),//face 6
        nx(-1, 0, 0, 2), y(0, 1, 0, 3), ny(0, -1, 0, 4), z(0, 0, 1, 5), nz(0, 0, -1, 6), xy(1, 1, 0, 7),//edge 12
        xny(1, -1, 0, 8), xz(1, 0, 1, 9), xnz(1, 0, -1, 10), nxy(-1, 1, 0, 11), nxny(-1, -1, 0, 12), nxz(-1, 0, 1, 13), nxnz(-1, 0, -1, 14), yz(0, 1, 1, 15), ynz(0, 1, -1, 16), nyz(0, -1, 1, 17), nynz(0, -1, -1, 18), xyz(1, 1, 1, 19),//corner 8
        xynz(1, 1, -1, 20), xnyz(1, -1, 1, 21), xnynz(1, -1, -1, 22), nxyz(-1, 1, 1, 23), nxynz(-1, 1, -1, 24), nxnyz(-1, -1, 1, 25), nxnynz(-1, -1, -1, 26);
        public final int Xoffset;
        public final int Yoffset;
        public final int Zoffset;
        public final int id;


        PDirection(int x, int y, int z, int id) {
            this.Xoffset = x;
            this.Yoffset = y;
            this.Zoffset = z;
            this.id = id;
        }
    }
}
