package github.forilusa.gtlendless.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import github.forilusa.gtlendless.config.ScannerConfig;

// 扫描器UI
public class ScannerConfigScreen extends Screen {
    private final ItemStack scannerStack;
    private Checkbox globalModeCheckbox;
    private Checkbox compactOutputCheckbox;
    private Checkbox renderModeCheckbox;

    public ScannerConfigScreen(ItemStack stack) {
        super(Component.translatable("gui.gtlendless.scanner_config.title"));
        this.scannerStack = stack;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        boolean globalMode = ScannerConfig.globalMode;
        boolean compactOutput = ScannerConfig.compactOutput;
        boolean renderMode = ScannerConfig.renderMode;

        int containerWidth = 240;
        int containerHeight = 180;
        int containerX = centerX - containerWidth / 2;
        int containerY = centerY - containerHeight / 2;

        int titleY = containerY + 15;
        int lineY = titleY + 15;
        int startY = lineY + 20;
        int rowHeight = 28;
        int labelX = containerX + 40;
        int checkboxX = containerX + 160;

        this.globalModeCheckbox = new Checkbox(
                checkboxX, startY,
                20, 20,
                Component.literal(""),
                globalMode,
                false
        );

        this.compactOutputCheckbox = new Checkbox(
                checkboxX, startY + rowHeight,
                20, 20,
                Component.literal(""),
                compactOutput,
                false
        );

        this.renderModeCheckbox = new Checkbox(
                checkboxX, startY + rowHeight * 2,
                20, 20,
                Component.literal(""),
                renderMode,
                false
        );

        int buttonWidth = 100;
        int buttonSpacing = 10;
        int totalButtonsWidth = buttonWidth * 2 + buttonSpacing;
        int buttonStartX = centerX - totalButtonsWidth / 2;
        int buttonY = startY + rowHeight * 3 + 10;

        Button saveButton = Button.builder(
                Component.translatable("gui.gtlendless.scanner_config.save"),
                button -> saveConfig()
        ).bounds(buttonStartX, buttonY, buttonWidth, 20).build();

        Button closeButton = Button.builder(
                Component.translatable("gui.gtlendless.scanner_config.close"),
                button -> this.onClose()
        ).bounds(buttonStartX + buttonWidth + buttonSpacing, buttonY, buttonWidth, 20).build();

        this.addRenderableWidget(globalModeCheckbox);
        this.addRenderableWidget(compactOutputCheckbox);
        this.addRenderableWidget(renderModeCheckbox);
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(closeButton);
    }

    private void saveConfig() {
        ScannerConfig.globalMode = globalModeCheckbox.selected();
        ScannerConfig.compactOutput = compactOutputCheckbox.selected();
        ScannerConfig.renderMode = renderModeCheckbox.selected();

        ScannerConfig.saveConfig();

        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.displayClientMessage(
                    Component.translatable("gui.gtlendless.scanner_config.saved"),
                    true
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int containerWidth = 240;
        int containerHeight = 180;
        int containerX = centerX - containerWidth / 2;
        int containerY = centerY - containerHeight / 2;

        guiGraphics.fill(containerX, containerY,
                containerX + containerWidth,
                containerY + containerHeight,
                0xAA000000);

        guiGraphics.renderOutline(containerX, containerY,
                containerWidth, containerHeight,
                0xFFCCCCCC);

        int titleY = containerY + 15;
        guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("gui.gtlendless.scanner_config.title"),
                centerX,
                titleY,
                0xFFFFD700
        );

        int lineY = titleY + 15;
        int lineStartX = containerX + 30;
        int lineEndX = containerX + containerWidth - 30;
        guiGraphics.hLine(lineStartX, lineEndX, lineY, 0xFF666666);

        int startY = lineY + 20;
        int rowHeight = 28;
        int labelX = containerX + 40;
        int checkboxX = containerX + 160;
        int statusX = checkboxX + 25;

        String globalModeStatus = globalModeCheckbox.selected() ? "§a开启" : "§7关闭";
        String compactOutputStatus = compactOutputCheckbox.selected() ? "§a开启" : "§7关闭";
        String renderModeStatus = renderModeCheckbox.selected() ? "§a开启" : "§7关闭";

        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.gtlendless.scanner_config.global_mode"),
                labelX,
                startY + 5,
                0xFFFFFFFF
        );

        guiGraphics.drawString(
                this.font,
                globalModeStatus,
                statusX,
                startY + 5,
                0xFFFFFFFF
        );

        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.gtlendless.scanner_config.compact_output"),
                labelX,
                startY + rowHeight + 5,
                0xFFFFFFFF
        );

        guiGraphics.drawString(
                this.font,
                compactOutputStatus,
                statusX,
                startY + rowHeight + 5,
                0xFFFFFFFF
        );

        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.gtlendless.scanner_config.render_mode"),
                labelX,
                startY + rowHeight * 2 + 5,
                0xFFFFFFFF
        );

        guiGraphics.drawString(
                this.font,
                renderModeStatus,
                statusX,
                startY + rowHeight * 2 + 5,
                0xFFFFFFFF
        );

        int hoverAreaWidth = 180;
        if (isMouseOver(guiGraphics, labelX, startY, hoverAreaWidth, 20, mouseX, mouseY)) {
            renderTooltip(guiGraphics,
                    Component.translatable("gui.gtlendless.scanner_config.global_mode.tooltip"),
                    mouseX, mouseY);
        } else if (isMouseOver(guiGraphics, labelX, startY + rowHeight, hoverAreaWidth, 20, mouseX, mouseY)) {
            renderTooltip(guiGraphics,
                    Component.translatable("gui.gtlendless.scanner_config.compact_output.tooltip"),
                    mouseX, mouseY);
        } else if (isMouseOver(guiGraphics, labelX, startY + rowHeight * 2, hoverAreaWidth, 20, mouseX, mouseY)) {
            renderTooltip(guiGraphics,
                    Component.translatable("gui.gtlendless.scanner_config.render_mode.tooltip"),
                    mouseX, mouseY);
        }
    }

    private boolean isMouseOver(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void renderTooltip(GuiGraphics guiGraphics, Component component, int mouseX, int mouseY) {
        guiGraphics.renderTooltip(this.font, this.font.split(component, 200), mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}