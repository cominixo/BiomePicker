package me.cominixo.biomepicker.mixin;

import me.cominixo.biomepicker.gui.BiomeSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Predicate;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    private static ServerWorld world;

    @Inject(method = "setupSpawn", at = @At("HEAD"))
    private static void setupSpawn(ServerWorld serverWorld, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld, CallbackInfo ci) {
        world = serverWorld;
    }

    @Redirect(method = "setupSpawn", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/SpawnLocating;findServerSpawnPoint(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/util/math/BlockPos;"
    ))
    private static BlockPos changeBlockPos(ServerWorld world, ChunkPos chunkPos) {

        BlockPos blockPos = new BlockPos(0, world.getSeaLevel(), 0);

        if (BiomeSelectionScreen.selectedBiome == null) {
            return blockPos;
        }

        for (Biome biome : BuiltinRegistries.BIOME) {

            if (BuiltinRegistries.BIOME.getId(biome) == BuiltinRegistries.BIOME.getId(BiomeSelectionScreen.selectedBiome)) {

                BlockPos foundBlockPos = world.locateBiome(world.getServer().getRegistryManager().get(Registry.BIOME_KEY).get(BuiltinRegistries.BIOME.getId(BiomeSelectionScreen.selectedBiome)), blockPos, 20000, 8);
                if (foundBlockPos != null) {
                    blockPos = foundBlockPos.mutableCopy();
                }
                break;

            }
        }
        return blockPos;
    }


}
