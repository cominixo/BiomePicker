package me.cominixo.biomepicker.mixin;

import me.cominixo.biomepicker.gui.BiomeSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    private static BlockPos newBlockPos;

    @Inject(method = "setupSpawn", at = @At("HEAD"))
    private static void setupSpawn(ServerWorld serverWorld, ServerWorldProperties serverWorldProperties, boolean bl, boolean bl2, boolean bl3, CallbackInfo ci) {
        BlockPos blockPos = new BlockPos(0, serverWorld.getSeaLevel(), 0);

        if (BiomeSelectionScreen.selectedBiome == null) {
            newBlockPos = blockPos;
            return;
        }

        for (Biome biome : BuiltinRegistries.BIOME) {

            if (BuiltinRegistries.BIOME.getId(biome) == BuiltinRegistries.BIOME.getId(BiomeSelectionScreen.selectedBiome)) {

                BlockPos foundBlockPos = serverWorld.locateBiome(serverWorld.getServer().getRegistryManager().get(Registry.BIOME_KEY).get(BuiltinRegistries.BIOME.getId(BiomeSelectionScreen.selectedBiome)), blockPos, 20000, 8);
                if (foundBlockPos == null) {
                    newBlockPos = blockPos;
                } else {
                    newBlockPos = foundBlockPos.mutableCopy();
                }
                break;

            } else {
                newBlockPos = blockPos;
            }
        }
    }

    @ModifyConstant(method = "setupSpawn", constant = @Constant(intValue = 0, ordinal = 0))
    private static int changeBlockX(int i) {
        return newBlockPos.getX();
    }

    @ModifyConstant(method = "setupSpawn", constant = @Constant(intValue = 0, ordinal = 1))
    private static int changeBlockZ(int i) {
        return newBlockPos.getZ();
    }

    @Inject(method = "method_31146(Lnet/minecraft/world/biome/Biome;)Z", at = @At("HEAD"), cancellable = true)
    private static void modifyAcceptableBiome(Biome biome, CallbackInfoReturnable<Boolean> cir) {

        MinecraftServer server = MinecraftClient.getInstance().getServer();

        if (server == null) {
            cir.setReturnValue(false);
            return;
        }

        if (BiomeSelectionScreen.selectedBiome == null) {
            return;
        }

        cir.setReturnValue(server.getOverworld().getRegistryManager().get(Registry.BIOME_KEY).getId(biome) == BuiltinRegistries.BIOME.getId(BiomeSelectionScreen.selectedBiome)
                           && biome.getCategory() != Biome.Category.NETHER
                           && biome.getCategory() != Biome.Category.THEEND
                           && biome.getCategory() != Biome.Category.NONE);
    }


}
