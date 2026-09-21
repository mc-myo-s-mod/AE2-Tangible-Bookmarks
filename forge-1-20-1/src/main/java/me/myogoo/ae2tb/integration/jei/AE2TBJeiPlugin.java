package me.myogoo.ae2tb.integration.jei;

import appeng.api.integrations.jei.IngredientConverters;
import appeng.api.stacks.GenericStack;
import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.init.AE2TBItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class AE2TBJeiPlugin implements IModPlugin {
    private static final String SEARCH_ALIAS = "ae2tb";

    @Override
    public ResourceLocation getPluginUid() {
        return AE2TB.makeId("aliases");
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAlias(VanillaTypes.ITEM_STACK,
                AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get().getDefaultInstance(), SEARCH_ALIAS);
    }

    public static GenericStack toGenericStack(ITypedIngredient<?> ingredient) {
        return convert(ingredient);
    }

    private static <T> GenericStack convert(ITypedIngredient<T> ingredient) {
        var converter = IngredientConverters.getConverter(ingredient.getType());
        return converter == null ? null : converter.getStackFromIngredient(ingredient.getIngredient());
    }
}
