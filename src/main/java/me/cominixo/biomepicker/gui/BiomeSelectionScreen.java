package me.cominixo.biomepicker.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Comparator;

public class BiomeSelectionScreen extends Screen {

    private final Screen parent;
    private BiomeSelectionScreen.BiomesListWidget biomeSelectionList;

    public static Biome selectedBiome;

    public BiomeSelectionScreen(Screen screen) {
        super(new TranslatableText("biomepicker.spawnbiome"));
        this.parent = screen;
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }

    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.biomeSelectionList = new BiomeSelectionScreen.BiomesListWidget();
        this.children.add(this.biomeSelectionList);
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, (buttonWidget) -> {

            this.client.openScreen(this.parent);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, (buttonWidget) -> {
            this.client.openScreen(this.parent);
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
            BuiltinRegistries.BIOME.getEntries().stream().sorted(Comparator.comparing((entry) -> (entry.getKey()).getValue().toString())).forEach((entry)
                    -> {
                        Biome biome = entry.getValue();

                        if (biome.getCategory() != Biome.Category.NETHER
                        && biome.getCategory() != Biome.Category.THEEND
                        && biome.getCategory() != Biome.Category.NONE) {
                            this.addEntry(new BiomeSelectionScreen.BiomesListWidget.BiomeItem(biome));
                        }

                    });
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
                    this.text = new TranslatableText(string);
                } else {
                    this.text = new LiteralText(WordUtils.capitalize(identifier.getPath().replace("_", " ")));
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
        }
    }

}
