package me.cominixo.biomepicker.mixin;

import me.cominixo.biomepicker.BiomePicker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Shadow protected abstract <T extends AbstractButtonWidget> T addButton(T button);

    @Inject(method = "init", at = @At("HEAD"))
    public void initBiomeButton(CallbackInfo ci) {

        if (BiomePicker.biomes.isEmpty()) {

            BiomePicker.biomes.add(null);

            for (Biome b : BuiltinRegistries.BIOME) {

                if (b.getCategory() != Biome.Category.NETHER
                        && b.getCategory() != Biome.Category.THEEND
                        && b != Biomes.THE_VOID) {
                    BiomePicker.biomes.add(BuiltinRegistries.BIOME.getId(b));
                }

            }
            BiomePicker.selectedBiome = null;

        }

        this.addButton(BiomePicker.getWidget(this.width));
    }

}
