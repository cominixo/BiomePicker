package me.cominixo.biomepicker.mixin;

import me.cominixo.biomepicker.gui.BiomeSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
        this.addButton(new ButtonWidget(width / 2 - 155, 209, 310, 20, new TranslatableText("biomepicker.spawnbiome"),
                (button) -> MinecraftClient.getInstance().openScreen(new BiomeSelectionScreen(MinecraftClient.getInstance().currentScreen))));
    }

}
