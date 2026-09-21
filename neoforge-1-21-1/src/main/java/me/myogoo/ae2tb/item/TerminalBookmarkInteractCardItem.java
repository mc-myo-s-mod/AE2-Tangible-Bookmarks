package me.myogoo.ae2tb.item;

import me.myogoo.ae2tb.client.TranslateKey;
import me.myogoo.myotus.api.ITerminalUpgradeCard;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TerminalBookmarkInteractCardItem extends Item implements ITerminalUpgradeCard {
    public TerminalBookmarkInteractCardItem() {
        super(new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(TranslateKey.TERMINAL_BOOKMARK_INTERACT_CARD_DESC.getTranslate()
                .withStyle(ChatFormatting.GRAY));
    }
}
