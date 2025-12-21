package github.forilusa.gtlendless.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import github.forilusa.gtlendless.GTLendless;
import github.forilusa.gtlendless.client.AnimatedTextManager;

import java.util.ArrayList;
import java.util.List;

public class MachineTransformationCategory implements IRecipeCategory<MachineTransformationRecipe> {

    public static final RecipeType<MachineTransformationRecipe> RECIPE_TYPE =
            RecipeType.create(GTLendless.MOD_ID, "machine_transformation", MachineTransformationRecipe.class);

    // 箭头路径
    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation(GTLendless.MOD_ID, "textures/gui/arrow.png");
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;

    private static final int BASE_WIDTH = 120;
    private static final int BASE_HEIGHT = 70;
    private static final int MIN_HEIGHT = 70;
    private static final int MAX_HEIGHT = 150;
    private static final int PADDING = 10;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final IGuiHelper guiHelper;

    private int width;
    private int height;
    private String currentLanguage = "";
    private boolean needsRecalculation = true;

    public MachineTransformationCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.width = BASE_WIDTH;
        this.height = BASE_HEIGHT;
        this.background = guiHelper.createBlankDrawable(width, height);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.ANVIL));

        this.arrow = guiHelper.drawableBuilder(ARROW_TEXTURE, 0, 0, ARROW_WIDTH, ARROW_HEIGHT)
                .setTextureSize(ARROW_WIDTH, ARROW_HEIGHT)
                .build();

        updateCurrentLanguage();
    }

    // 更新语言重新计算
    private void updateCurrentLanguage() {
        String newLanguage = Minecraft.getInstance().getLanguageManager().getSelected();
        if (!newLanguage.equals(currentLanguage)) {
            currentLanguage = newLanguage;
            needsRecalculation = true;
        }
    }

    private void calculateHeight() {
        if (Minecraft.getInstance().font != null) {
            MutableComponent text = Component.translatable("gtlendless.jei.transformation_text")
                    .withStyle(ChatFormatting.BOLD)
                    .withStyle(style -> style.withColor(0x000000));

            String textString = text.getString();
            List<String> wrappedLines = wrapText(textString, BASE_WIDTH - 2 * PADDING);

            int lineHeight = Minecraft.getInstance().font.lineHeight + 2;
            int textHeight = wrappedLines.size() * lineHeight;
            int requiredHeight = 40 + textHeight + PADDING;

            this.height = Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, requiredHeight));
            needsRecalculation = false;
        }
    }

    @Override
    public RecipeType<MachineTransformationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return AnimatedTextManager.createDynamicTranslatedRainbowWave("gtlendless.jei.category.machine_transformation");
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        // 检查是否需要重新计算
        updateCurrentLanguage();
        if (needsRecalculation) {
            calculateHeight();
        }
        return height;
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        updateCurrentLanguage();
        if (needsRecalculation) {
            calculateHeight();
        }
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineTransformationRecipe recipe, IFocusGroup focuses) {
        int slotSize = 16;
        int centerX = width / 2;
        int slotSpacing = 24;

        int inputX = centerX - slotSpacing - slotSize;
        int outputX = centerX + slotSpacing;

        builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, inputX, 15)
                .addItemStack(recipe.input());

        builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT, outputX, 15)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(MachineTransformationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // 检测
        updateCurrentLanguage();
        if (needsRecalculation) {
            calculateHeight();
        }

        int centerX = width / 2;
        int arrowX = centerX - ARROW_WIDTH / 2;

        arrow.draw(guiGraphics, arrowX, 15);

        if (Minecraft.getInstance().font != null) {
            MutableComponent text = Component.translatable("gtlendless.jei.transformation_text")
                    .withStyle(ChatFormatting.BOLD)
                    .withStyle(style -> style.withColor(0x000000));

            String textString = text.getString();
            List<String> wrappedLines = wrapText(textString, width - 2 * PADDING);

            int firstLineWidth = 0;
            if (!wrappedLines.isEmpty()) {
                MutableComponent firstLineComponent = Component.literal(wrappedLines.get(0))
                        .withStyle(ChatFormatting.BOLD)
                        .withStyle(style -> style.withColor(0x000000));
                firstLineWidth = Minecraft.getInstance().font.width(firstLineComponent);
            }

            int startY = 40;
            int lineHeight = Minecraft.getInstance().font.lineHeight + 2;

            for (int i = 0; i < wrappedLines.size(); i++) {
                String line = wrappedLines.get(i);
                MutableComponent lineComponent = Component.literal(line)
                        .withStyle(ChatFormatting.BOLD)
                        .withStyle(style -> style.withColor(0x000000));

                int lineWidth = Minecraft.getInstance().font.width(lineComponent);
                int textX;

                // 对齐
                if (i == wrappedLines.size() - 1 && wrappedLines.size() > 1) {
                    textX = (width - firstLineWidth) / 2;
                } else {
                    textX = (width - lineWidth) / 2;
                }

                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        lineComponent,
                        textX, startY + i * lineHeight, 0x000000, false
                );
            }
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        Minecraft minecraft = Minecraft.getInstance();

        StringBuilder currentLine = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String testLine = currentLine.toString() + c;
            int lineWidth = minecraft.font.width(testLine);

            if (lineWidth <= maxWidth) {
                currentLine.append(c);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(String.valueOf(c));
                } else {
                    lines.add(String.valueOf(c));
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}