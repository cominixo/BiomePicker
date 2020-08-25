package me.cominixo.biomepicker;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BiomePicker {

    public static int currentIndex = 0;
    public static List<Identifier> biomes = new ArrayList<>();

    public static Biome selectedBiome;

    public static ButtonWidget getWidget(int width) {
        return new ButtonWidget(width / 2 - 155, 209, 310, 20, new TranslatableText("biomepicker.spawnbiome"), (button) -> {
            if (Screen.hasShiftDown()) {
                currentIndex -= 1;
            } else {
                currentIndex += 1;
            }

            currentIndex %= biomes.size();

            currentIndex = currentIndex >= 0 ? currentIndex : currentIndex + biomes.size();

            Identifier id = biomes.get(Math.abs(currentIndex));

            if (id == null) {
                selectedBiome = null;
            }

            selectedBiome = DynamicRegistryManager.create().get(Registry.BIOME_KEY).get(id);


        }) {
            public Text getMessage() {
                String biomeName;
                if (biomes.get(BiomePicker.currentIndex) == null) {
                    biomeName = I18n.translate("biomepicker.default");
                } else {
                    biomeName = WordUtils.capitalize(biomes.get(currentIndex).getPath().replace("_", " "));
                }

                return (new TranslatableText("biomepicker.spawnbiome")).append(": ").append(biomeName);
            }

            @Override
            public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.currentScreen != null) {
                    List<Text> tooltip = Arrays.asList(new TranslatableText("biomepicker.spawnbiome.tooltip.0"), new TranslatableText("biomepicker.spawnbiome.tooltip.1"));
                    client.currentScreen.renderTooltip(matrices, tooltip, mouseX, mouseY);
                }

            }
        };
    }

}
