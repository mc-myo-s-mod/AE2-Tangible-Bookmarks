package me.myogoo.ae2tb.integration.jei;

import appeng.api.stacks.GenericStack;
import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.init.AE2TBItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.lang.reflect.Method;

@JeiPlugin
public final class AE2TBJeiPlugin implements IModPlugin {
    private static final String SEARCH_ALIAS = "ae2tb";
    private static final Method AE2_JEI_CONVERTER = findAe2JeiConverter();

    @Override
    public ResourceLocation getPluginUid() {
        return AE2TB.makeId("aliases");
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAlias(AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get(), SEARCH_ALIAS);
    }

    public static GenericStack toGenericStack(ITypedIngredient<?> ingredient) {
        if (AE2_JEI_CONVERTER != null) {
            try {
                var converted = AE2_JEI_CONVERTER.invoke(null, ingredient);
                if (converted instanceof GenericStack genericStack) {
                    return genericStack;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        var value = ingredient.getIngredient();
        if (value instanceof ItemStack itemStack) {
            return GenericStack.fromItemStack(itemStack);
        }
        if (value instanceof FluidStack fluidStack) {
            return GenericStack.fromFluidStack(fluidStack);
        }
        return null;
    }

    private static Method findAe2JeiConverter() {
        try {
            var helper = Class.forName(
                    "tamaized.ae2jeiintegration.integration.modules.jei.GenericEntryStackHelper");
            return helper.getMethod("ingredientToStack", ITypedIngredient.class);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
