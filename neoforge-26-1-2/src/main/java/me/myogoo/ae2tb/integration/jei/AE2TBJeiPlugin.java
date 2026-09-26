package me.myogoo.ae2tb.integration.jei;

import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.init.AE2TBItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class AE2TBJeiPlugin implements IModPlugin {
    private static final String SEARCH_ALIAS = "ae2tb";

    @Override
    public Identifier getPluginUid() {
        return AE2TB.makeId("aliases");
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAlias(new ItemStack(AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()), SEARCH_ALIAS);
    }
}
