package me.cominixo.biomepicker.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.apache.commons.lang3.text.WordUtils;

public class BiomeSelectionScreen extends Screen {

    private final Screen parent;
    private BiomeSelectionScreen.BiomesListWidget biomeSelectionList;

    public static Biome selectedBiome;

    public BiomeSelectionScreen(Screen screen) {
        super(Text.translatable("biomepicker.spawnbiome"));
        this.parent = screen;
    }

    public void onClose() {
        this.client.setScreen(this.parent);
    }

    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.biomeSelectionList = new BiomeSelectionScreen.BiomesListWidget();
        this.addDrawableChild(this.biomeSelectionList);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, (buttonWidget) -> {

            this.client.setScreen(this.parent);
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, (buttonWidget) -> {
            this.client.setScreen(this.parent);
        }));
        this.biomeSelectionList.setSelected(null);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        this.biomeSelectionList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    class BiomesListWidget extends AlwaysSelectedEntryListWidget<BiomesListWidget.BiomeItem> {
        private BiomesListWidget() {
            super(BiomeSelectionScreen.this.client, BiomeSelectionScreen.this.width, BiomeSelectionScreen.this.height, 40, BiomeSelectionScreen.this.height - 37, 16);
            MultiNoiseBiomeSource.Preset.OVERWORLD.stream().forEach((key) -> this.addEntry(new BiomeItem(BuiltinRegistries.BIOME.get(key))));

        }

        protected boolean isFocused() {
            return BiomeSelectionScreen.this.getFocused() == this;
        }

        public void setSelected(BiomeSelectionScreen.BiomesListWidget.BiomeItem biomeItem) {

            if (biomeItem == null) {
                selectedBiome = null;
            } else {
                selectedBiome = biomeItem.biome;
                super.setSelected(biomeItem);
            }
        }

        @Environment(EnvType.CLIENT)
        class BiomeItem extends AlwaysSelectedEntryListWidget.Entry<BiomeSelectionScreen.BiomesListWidget.BiomeItem> {
            private final Biome biome;
            private final Text text;

            public BiomeItem(Biome biome) {
                this.biome = biome;
                Identifier identifier = BuiltinRegistries.BIOME.getId(biome);
                String string = "biome." + identifier.getNamespace() + "." + identifier.getPath();
                if (Language.getInstance().hasTranslation(string)) {
                    this.text = Text.translatable(string);
                } else {
                    this.text = Text.literal(WordUtils.capitalize(identifier.getPath().replace("_", " ")));
                }

            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.drawTextWithShadow(matrices, BiomeSelectionScreen.this.textRenderer, this.text, x + 5, y + 2, 0xFFFFFF);
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    BiomeSelectionScreen.BiomesListWidget.this.setSelected(this);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Text getNarration() {
                return text;
            }
        }
    }

}
