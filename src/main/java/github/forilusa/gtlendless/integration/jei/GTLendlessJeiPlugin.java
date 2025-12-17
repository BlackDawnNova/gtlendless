package github.forilusa.gtlendless.integration.jei;

import github.forilusa.gtlendless.GTLendless;
import github.forilusa.gtlendless.block.custom.ModBlocks; // 正确导入 ModBlocks
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class GTLendlessJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(GTLendless.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        try {
            ItemStack pseudoMachineStack = getPseudoMachineStack();
            if (!pseudoMachineStack.isEmpty()) {
                registration.addRecipeCatalyst(pseudoMachineStack, RecipeTypes.SMELTING);
                registration.addRecipeCatalyst(pseudoMachineStack, MachineTransformationCategory.RECIPE_TYPE);
                System.out.println("GTLendless: Successfully registered pseudo machine as catalyst");
            } else {
                System.out.println("GTLendless: Pseudo machine stack is empty");
            }
        } catch (Exception e) {
            System.out.println("GTLendless: Error registering recipe catalyst: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<MachineTransformationRecipe> recipes = createMachineTransformationRecipes();
        if (!recipes.isEmpty()) {
            registration.addRecipes(MachineTransformationCategory.RECIPE_TYPE, recipes);
            System.out.println("GTLendless: Registered machine transformation recipes");
        }

        System.out.println("GTLendless: JEI recipe registration completed");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MachineTransformationCategory(registration.getJeiHelpers().getGuiHelper()));
        System.out.println("GTLendless: JEI category registration completed");
    }

    private List<MachineTransformationRecipe> createMachineTransformationRecipes() {
        List<MachineTransformationRecipe> recipes = new ArrayList<>();

        try {
            ItemStack pseudoMachine = getPseudoMachineStack();
            ItemStack realMachine = getRealMachineStack();

            if (!pseudoMachine.isEmpty() && !realMachine.isEmpty()) {
                recipes.add(new MachineTransformationRecipe(pseudoMachine, realMachine));
                System.out.println("GTLendless: Created machine transformation recipe");
            } else {
                System.out.println("GTLendless: Could not create transformation recipe - pseudo: " +
                        pseudoMachine.isEmpty() + ", real: " + realMachine.isEmpty());
            }
        } catch (Exception e) {
            System.out.println("GTLendless: Error creating transformation recipes: " + e.getMessage());
            e.printStackTrace();
        }

        return recipes;
    }

    private ItemStack getPseudoMachineStack() {
        try {
            // 直接从 ModBlocks 获取，不依赖单例模式
            if (ModBlocks.PSEUDO_STEAM_FINAL_FURNACE != null && ModBlocks.PSEUDO_STEAM_FINAL_FURNACE.get() != null) {
                ItemStack stack = new ItemStack(ModBlocks.PSEUDO_STEAM_FINAL_FURNACE.get());
                System.out.println("GTLendless: Got pseudo machine stack: " + stack);
                return stack;
            } else {
                System.out.println("GTLendless: Pseudo machine block is null in ModBlocks");
            }
        } catch (Exception e) {
            System.out.println("GTLendless: Error getting pseudo machine stack: " + e.getMessage());
            e.printStackTrace();
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getRealMachineStack() {
        try {
            ResourceLocation realMachineId = new ResourceLocation("gtlendless", "steam_final_furnace");
            var realMachine = com.gregtechceu.gtceu.api.registry.GTRegistries.MACHINES.get(realMachineId);
            if (realMachine != null) {
                ItemStack stack = realMachine.asStack();
                System.out.println("GTLendless: Got real machine stack: " + stack);
                return stack;
            } else {
                System.out.println("GTLendless: Real machine not found: " + realMachineId);
            }
        } catch (Exception e) {
            System.out.println("GTLendless: Error getting real machine stack: " + e.getMessage());
            e.printStackTrace();
        }
        return ItemStack.EMPTY;
    }
}