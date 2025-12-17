package github.forilusa.gtlendless.client.config;

import github.forilusa.gtlendless.config.GTLendlessConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToolListConfigScreen extends Screen {
    private final Screen parent;
    private EditBox[] toolFields;

    public ToolListConfigScreen(Screen parent) {
        super(Component.translatable("config.gtlendless.screen.toolList"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int y = this.height / 4;


        this.addRenderableWidget(Button.builder(
                        Component.translatable("config.gtlendless.option.toolList"),
                        button -> {})
                .bounds(centerX - 150, y - 25, 300, 20)
                .build()).active = false;

        y += 5;


        toolFields = new EditBox[8];

        for (int i = 0; i < toolFields.length; i++) {
            int fieldY = y + (i * 25);


            this.addRenderableWidget(Button.builder(
                            Component.translatable("config.gtlendless.option.tool" + (i + 1)),
                            button -> {})
                    .bounds(centerX - 180, fieldY, 60, 20)
                    .build()).active = false;


            toolFields[i] = new EditBox(this.font, centerX - 115, fieldY, 230, 20, Component.empty());
            toolFields[i].setMaxLength(100);


            String toolValue = getToolValue(i);
            toolFields[i].setValue(toolValue != null ? toolValue : "");

            this.addRenderableWidget(toolFields[i]);
        }
    }


    private String getToolValue(int index) {
        switch (index) {
            case 0: return GTLendlessConfig.INSTANCE.toolListSettings.tool1;
            case 1: return GTLendlessConfig.INSTANCE.toolListSettings.tool2;
            case 2: return GTLendlessConfig.INSTANCE.toolListSettings.tool3;
            case 3: return GTLendlessConfig.INSTANCE.toolListSettings.tool4;
            case 4: return GTLendlessConfig.INSTANCE.toolListSettings.tool5;
            case 5: return GTLendlessConfig.INSTANCE.toolListSettings.tool6;
            case 6: return GTLendlessConfig.INSTANCE.toolListSettings.tool7;
            case 7: return GTLendlessConfig.INSTANCE.toolListSettings.tool8;
            default: return "";
        }
    }


    private void setToolValue(int index, String value) {
        if (value == null) value = "";

        switch (index) {
            case 0: GTLendlessConfig.INSTANCE.toolListSettings.tool1 = value; break;
            case 1: GTLendlessConfig.INSTANCE.toolListSettings.tool2 = value; break;
            case 2: GTLendlessConfig.INSTANCE.toolListSettings.tool3 = value; break;
            case 3: GTLendlessConfig.INSTANCE.toolListSettings.tool4 = value; break;
            case 4: GTLendlessConfig.INSTANCE.toolListSettings.tool5 = value; break;
            case 5: GTLendlessConfig.INSTANCE.toolListSettings.tool6 = value; break;
            case 6: GTLendlessConfig.INSTANCE.toolListSettings.tool7 = value; break;
            case 7: GTLendlessConfig.INSTANCE.toolListSettings.tool8 = value; break;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);


        guiGraphics.drawString(this.font,
                Component.translatable("config.gtlendless.option.useDefaultTool"),
                this.width / 2 - 150, this.height / 4 - 40, 0xAAAAAA, false);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        for (int i = 0; i < toolFields.length; i++) {
            String fieldValue = toolFields[i].getValue();
            setToolValue(i, fieldValue);
        }

        this.minecraft.setScreen(this.parent);
    }
}