package me.myogoo.ae2tb.item;

import me.myogoo.ae2tb.client.TranslateKey;
import me.myogoo.myotus.api.ITerminalUpgradeCard;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

public class TerminalBookmarkInteractCardItem extends Item implements ITerminalUpgradeCard {
    public TerminalBookmarkInteractCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
        tooltipComponents.accept(TranslateKey.TERMINAL_BOOKMARK_INTERACT_CARD_DESC.getTranslate()
                .withStyle(ChatFormatting.GRAY));
    }
}
