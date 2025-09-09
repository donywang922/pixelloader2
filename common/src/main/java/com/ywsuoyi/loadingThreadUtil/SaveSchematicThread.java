package com.ywsuoyi.loadingThreadUtil;

import com.ywsuoyi.PixelLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.datafixers.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

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

            // 创建NBT结构
            CompoundTag rootTag = new CompoundTag();

            // 计算结构尺寸
            if (genBlocks.isEmpty()) {
                this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error.empty"));
                onend(false);
                return;
            }

            BlockPos minPos = genBlocks.getFirst().getA();
            BlockPos maxPos = genBlocks.getFirst().getA();

            // 寻找边界
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

            int width = maxPos.getX() - minPos.getX() + 1;
            int height = maxPos.getY() - minPos.getY() + 1;
            int length = maxPos.getZ() - minPos.getZ() + 1;

            // 设置基本信息
            rootTag.putShort("Width", (short) width);
            rootTag.putShort("Height", (short) height);
            rootTag.putShort("Length", (short) length);
            rootTag.putString("Materials", "Alpha");

            // 创建方块数据
            ListTag blocksList = new ListTag();

            int processed = 0;
            int total = genBlocks.size();

            for (Tuple<BlockPos, BlockState> entry : genBlocks) {
                if (state == State.end) {
                    onend(true);
                    return;
                }
                BlockPos pos = entry.getA();
                BlockState blockState = entry.getB();

                CompoundTag blockTag = new CompoundTag();
                blockTag.putString("Name", blockState.getBlock().getDescriptionId());

                // 相对坐标
                blockTag.putInt("x", pos.getX() - minPos.getX());
                blockTag.putInt("y", pos.getY() - minPos.getY());
                blockTag.putInt("z", pos.getZ() - minPos.getZ());

                // 方块状态属性
                if (!blockState.getProperties().isEmpty()) {
                    CompoundTag propertiesTag = new CompoundTag();
                    blockState.getProperties().forEach(property -> {
                        propertiesTag.putString(property.getName(), blockState.getValue(property).toString());
                    });
                    blockTag.put("Properties", propertiesTag);
                }

                blocksList.add(blockTag);

                processed++;
                if (processed % 100 == 0 || processed == total) {
                    this.message = Component.literal(processed + "/" + total);
                }
            }

            rootTag.put("Blocks", blocksList);

            // 保存文件
            File outputFile = new File(schematicsDir, fileName + ".nbt");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                NbtIo.writeCompressed(rootTag, fos);
            }
            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.saved", fileName));
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to save nbt: {}", e.getMessage());
            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error", e.getMessage()));
        } catch (Exception e) {
            PixelLoader.logger.error("Failed to save nbt: {}", e.getMessage());
            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error", e.getMessage()));
        }
        onend(false);
    }
}