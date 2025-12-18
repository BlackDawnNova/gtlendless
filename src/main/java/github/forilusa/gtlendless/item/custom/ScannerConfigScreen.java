package github.forilusa.gtlendless.item.custom;

import github.forilusa.gtlendless.config.ScannerConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

//  扫描器UI
public class ScannerConfigScreen extends Screen {
    private final ItemStack scannerStack;
    private Checkbox globalModeCheckbox;
    private Checkbox compactOutputCheckbox;
    private Checkbox renderModeCheckbox;
    private Checkbox screenErrorDisplayCheckbox;

    public ScannerConfigScreen(ItemStack stack) {
        super(Component.translatable("gui.gtlendless.scanner_config.title"));
        this.scannerStack = stack;
    }

    @Override
    protected void init() {
        super.init();

        int screenWidth = this.width;
        int screenHeight = this.height;

        int containerWidth = Math.min(320, (int) (screenWidth * 0.6f));
        int containerHeight = Math.min(240, (int) (screenHeight * 0.6f));
        int containerX = (screenWidth - containerWidth) / 2;
        int containerY = (screenHeight - containerHeight) / 2;

        int titleHeight = 50;
        int contentHeight = containerHeight - titleHeight - 70;
        int buttonAreaHeight = 60;

        boolean globalMode = ScannerConfig.globalMode;
        boolean compactOutput = ScannerConfig.compactOutput;
        boolean renderMode = ScannerConfig.renderMode;
        boolean screenErrorDisplay = ScannerConfig.screenErrorDisplay;

        int numOptions = 4;
        int optionSpacing = (contentHeight - (numOptions * 24)) / (numOptions + 1);
        optionSpacing = Math.max(8, optionSpacing);

        int startY = containerY + titleHeight + optionSpacing + 10;
        int labelX = containerX + 30;
        int checkboxX = containerX + containerWidth - 60;

        this.globalModeCheckbox = new Checkbox(
                checkboxX, startY,
                20, 20,
                Component.literal(""),
                globalMode,
                false
        );

        this.compactOutputCheckbox = new Checkbox(
                checkboxX, startY + 24 + optionSpacing,
                20, 20,
                Component.literal(""),
                compactOutput,
                false
        );

        this.renderModeCheckbox = new Checkbox(
                checkboxX, startY + (24 + optionSpacing) * 2,
                20, 20,
                Component.literal(""),
                renderMode,
                false
        );

        this.screenErrorDisplayCheckbox = new Checkbox(
                checkboxX, startY + (24 + optionSpacing) * 3,
                20, 20,
                Component.literal(""),
                screenErrorDisplay,
                false
        );

        int buttonY = containerY + containerHeight - buttonAreaHeight / 2 - 10;
        int buttonWidth = Math.min(120, containerWidth / 3);
        int buttonSpacing = Math.max(15, containerWidth / 15);
        int totalButtonsWidth = buttonWidth * 2 + buttonSpacing;
        int buttonStartX = containerX + (containerWidth - totalButtonsWidth) / 2;

        Button saveButton = Button.builder(
                Component.translatable("gui.gtlendless.scanner_config.save"),
                button -> saveConfig()
        ).bounds(buttonStartX, buttonY, buttonWidth, 25).build();

        Button closeButton = Button.builder(
                Component.translatable("gui.gtlendless.scanner_config.close"),
                button -> this.onClose()
        ).bounds(buttonStartX + buttonWidth + buttonSpacing, buttonY, buttonWidth, 25).build();

        this.addRenderableWidget(globalModeCheckbox);
        this.addRenderableWidget(compactOutputCheckbox);
        this.addRenderableWidget(renderModeCheckbox);
        this.addRenderableWidget(screenErrorDisplayCheckbox);
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(closeButton);
    }

    private void saveConfig() {
        ScannerConfig.globalMode = globalModeCheckbox.selected();
        ScannerConfig.compactOutput = compactOutputCheckbox.selected();
        ScannerConfig.renderMode = renderModeCheckbox.selected();
        ScannerConfig.screenErrorDisplay = screenErrorDisplayCheckbox.selected();

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

        int screenWidth = this.width;
        int screenHeight = this.height;

        int containerWidth = Math.min(320, (int) (screenWidth * 0.6f));
        int containerHeight = Math.min(240, (int) (screenHeight * 0.6f));
        int containerX = (screenWidth - containerWidth) / 2;
        int containerY = (screenHeight - containerHeight) / 2;

        int titleHeight = 50;
        int contentHeight = containerHeight - titleHeight - 70;
        int buttonAreaHeight = 60;

        guiGraphics.fill(containerX, containerY,
                containerX + containerWidth,
                containerY + containerHeight,
                0xCC000000);

        guiGraphics.renderOutline(containerX, containerY,
                containerWidth, containerHeight,
                0xFFCCCCCC);

        guiGraphics.renderOutline(containerX + 1, containerY + 1,
                containerWidth - 2, containerHeight - 2,
                0x33FFFFFF);

        int titleY = containerY + 25;
        guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("gui.gtlendless.scanner_config.title"),
                containerX + containerWidth / 2,
                titleY,
                0xFFFFD700
        );

        int lineY = titleY + 25;
        int lineStartX = containerX + 20;
        int lineEndX = containerX + containerWidth - 20;
        guiGraphics.hLine(lineStartX, lineEndX, lineY, 0xFF888888);

        int numOptions = 4;
        int optionSpacing = (contentHeight - (numOptions * 24)) / (numOptions + 1);
        optionSpacing = Math.max(8, optionSpacing);

        int startY = containerY + titleHeight + optionSpacing + 10;
        int labelX = containerX + 30;
        int checkboxX = containerX + containerWidth - 60;
        int statusX = checkboxX + 25;

        String globalModeStatus = globalModeCheckbox.selected() ? "§a开启" : "§7关闭";
        String compactOutputStatus = compactOutputCheckbox.selected() ? "§a开启" : "§7关闭";
        String renderModeStatus = renderModeCheckbox.selected() ? "§a开启" : "§7关闭";
        String screenErrorDisplayStatus = screenErrorDisplayCheckbox.selected() ? "§a开启" : "§7关闭";

        for (int i = 0; i < numOptions; i++) {
            int yPos = startY + (24 + optionSpacing) * i + 5;
            String label, status;

            switch (i) {
                case 0:
                    label = Component.translatable("gui.gtlendless.scanner_config.global_mode").getString();
                    status = globalModeStatus;
                    break;
                case 1:
                    label = Component.translatable("gui.gtlendless.scanner_config.compact_output").getString();
                    status = compactOutputStatus;
                    break;
                case 2:
                    label = Component.translatable("gui.gtlendless.scanner_config.render_mode").getString();
                    status = renderModeStatus;
                    break;
                case 3:
                    label = Component.translatable("gui.gtlendless.scanner_config.screen_error_display").getString();
                    status = screenErrorDisplayStatus;
                    break;
                default:
                    label = "";
                    status = "";
            }

            guiGraphics.drawString(this.font, label, labelX, yPos, 0xFFFFFFFF);
            guiGraphics.drawString(this.font, status, statusX, yPos, 0xFFFFFFFF);
        }

        int buttonAreaY = containerY + containerHeight - buttonAreaHeight;
        guiGraphics.fill(containerX, buttonAreaY,
                containerX + containerWidth, buttonAreaY + buttonAreaHeight,
                0x33000000);

        for (int i = 0; i < numOptions; i++) {
            int yPos = startY + (24 + optionSpacing) * i;
            int hoverAreaWidth = containerWidth - 100;
            int hoverHeight = 24;

            Component tooltip = null;
            switch (i) {
                case 0:
                    tooltip = Component.translatable("gui.gtlendless.scanner_config.global_mode.tooltip");
                    break;
                case 1:
                    tooltip = Component.translatable("gui.gtlendless.scanner_config.compact_output.tooltip");
                    break;
                case 2:
                    tooltip = Component.translatable("gui.gtlendless.scanner_config.render_mode.tooltip");
                    break;
                case 3:
                    tooltip = Component.translatable("gui.gtlendless.scanner_config.screen_error_display.tooltip");
                    break;
            }

            if (tooltip != null && isMouseOver(labelX, yPos, hoverAreaWidth, hoverHeight, mouseX, mouseY)) {
                renderTooltip(guiGraphics, tooltip, mouseX, mouseY);
            }
        }
    }

    private boolean isMouseOver(int x, int y, int width, int height, int mouseX, int mouseY) {
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