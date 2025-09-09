package com.ywsuoyi.loadingThreadUtil;

import com.ywsuoyi.PixelLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SaveSchematicThread extends BaseThread {
    private final LinkedList<Tuple<BlockPos, BlockState>> genBlocks;
    private final String fileName;

    public SaveSchematicThread(Player player, LinkedList<Tuple<BlockPos, BlockState>> genBlocks, String fileName) {
        super(player);
        this.genBlocks = genBlocks;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            // 创建schematics目录
            File schematicsDir = new File("./schematics");
            if (!schematicsDir.exists()) {
                schematicsDir.mkdirs();
            }

            if (genBlocks.isEmpty()) {
                this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error.empty"));
                onend(false);
                return;
            }

            CompoundTag rootTag = new CompoundTag();
            NbtUtils.addCurrentDataVersion(rootTag);
            rootTag.putString("author", "PixelLoader");
            rootTag.put("entities", new ListTag());

            BlockPos minPos = genBlocks.getFirst().getA();
            BlockPos maxPos = genBlocks.getFirst().getA();
            for (Tuple<BlockPos, BlockState> entry : genBlocks) {
                BlockPos pos = entry.getA();
                minPos = new BlockPos(
                        Math.min(minPos.getX(), pos.getX()),
                        Math.min(minPos.getY(), pos.getY()),
                        Math.min(minPos.getZ(), pos.getZ())
                );
                maxPos = new BlockPos(
                        Math.max(maxPos.getX(), pos.getX()),
                        Math.max(maxPos.getY(), pos.getY()),
                        Math.max(maxPos.getZ(), pos.getZ())
                );
            }

            SimplePalette simplePalette = new SimplePalette();
            ListTag blocks = new ListTag();

            int processed = 0;
            int total = genBlocks.size();

            for (Tuple<BlockPos, BlockState> entry : genBlocks) {
                BlockPos pos = entry.getA().subtract(minPos);
                BlockState state = entry.getB();
                CompoundTag block = new CompoundTag();
                block.put("pos", this.newIntegerList(pos.getX(), pos.getY(), pos.getZ()));
                int k = simplePalette.idFor(state);
                block.putInt("state", k);
                blocks.add(block);
                processed++;
                this.message = Component.literal(processed + "/" + total);
            }

            int width = maxPos.getX() - minPos.getX() + 1;
            int height = maxPos.getY() - minPos.getY() + 1;
            int length = maxPos.getZ() - minPos.getZ() + 1;
            rootTag.put("size", this.newIntegerList(width, height, length));

            ListTag palette = new ListTag();
            for (BlockState blockState : simplePalette) {
                palette.add(NbtUtils.writeBlockState(blockState));
            }
            rootTag.put("palette", palette);
            rootTag.put("blocks", blocks);

            // 保存文件
            File outputFile = new File(schematicsDir, fileName + ".nbt");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                NbtIo.writeCompressed(rootTag, fos);
            }

            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.saved", fileName + ".nbt"));

        } catch (IOException e) {
            PixelLoader.logger.error("Failed to save structure: {}", e.getMessage());
            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error", e.getMessage()));
        } catch (Exception e) {
            PixelLoader.logger.error("Unexpected error saving structure: {}", e.getMessage());
            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error", e.getMessage()));
        }
        onend(false);
    }

    private ListTag newIntegerList(int... is) {
        ListTag listTag = new ListTag();

        for (int i : is) {
            listTag.add(IntTag.valueOf(i));
        }

        return listTag;
    }

    static class SimplePalette implements Iterable<BlockState> {
        public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        private final IdMapper<BlockState> ids = new IdMapper<>(16);
        private int lastId;

        public int idFor(BlockState blockState) {
            int i = this.ids.getId(blockState);
            if (i == -1) {
                i = this.lastId++;
                this.ids.addMapping(blockState, i);
            }

            return i;
        }

        @Nullable
        public BlockState stateFor(int i) {
            BlockState blockState = this.ids.byId(i);
            return blockState == null ? DEFAULT_BLOCK_STATE : blockState;
        }

        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(BlockState blockState, int i) {
            this.ids.addMapping(blockState, i);
        }
    }
}