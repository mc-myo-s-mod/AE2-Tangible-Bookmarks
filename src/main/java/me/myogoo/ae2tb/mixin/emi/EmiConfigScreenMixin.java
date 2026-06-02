package me.myogoo.ae2tb.mixin.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.screen.ConfigScreen;
import dev.emi.emi.screen.widget.config.*;
import me.myogoo.ae2tb.integration.emi.AE2TBEmiBind;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ConfigScreen.class)
public abstract class EmiConfigScreenMixin extends Screen {

    @Shadow
    public ListWidget list;
    @Shadow
    private ConfigSearch search;

    protected EmiConfigScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    public static List<ClientTooltipComponent> getFieldTooltip(Field field) {
        return null;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addCustomBinds(CallbackInfo ci) {
        Supplier<String> searchSupplier = () -> search.getSearch();

        String lastGroup = "";
        GroupNameWidget lastGroupWidget = null;
        EmiConfig.ConfigGroup currentGroup = null;
        SubGroupNameWidget currentSubGroupWidget = null;

        for (Field field : AE2TBEmiBind.class.getFields()) {
            EmiConfig.ConfigValue annot = field.getAnnotation(EmiConfig.ConfigValue.class);
            if (annot != null && field.getType() == EmiBind.class) {
                try {
                    String group = annot.value().split("\\.")[0];

                    if (!group.equals(lastGroup)) {
                        lastGroup = group;
                        Component text = EmiPort.translatable("config.emi.group." + group.replace('-', '_'));
                        lastGroupWidget = new GroupNameWidget(group, text);
                        list.addEntry(lastGroupWidget);
                    }
                    EmiConfig.ConfigGroup configGroup = field.getAnnotation(EmiConfig.ConfigGroup.class);
                    if (configGroup != null) {
                        currentGroup = configGroup;
                        Component text = EmiPort
                                .translatable("config.emi.group." + configGroup.value().replace('-', '_'));
                        currentSubGroupWidget = new SubGroupNameWidget(configGroup.value(), text);
                        currentSubGroupWidget.parent = lastGroupWidget;
                        list.addEntry(currentSubGroupWidget);
                    }

                    EmiBind bind = (EmiBind) field.get(null);
                    var entry = new EmiBindWidget(
                            (ConfigScreen) (Object) this,
                            getFieldTooltip(field),
                            searchSupplier,
                            bind);
                    boolean endGroup = field.getAnnotation(EmiConfig.ConfigGroupEnd.class) != null;

                    if (entry != null) {
                        entry.group = currentGroup;
                        entry.endGroup = endGroup;
                        list.addEntry(entry);
                        if (lastGroupWidget != null) {
                            lastGroupWidget.children.add(entry);
                            entry.parentGroups.add(lastGroupWidget);
                        }
                        if (currentSubGroupWidget != null) {
                            currentSubGroupWidget.children.add(entry);
                            entry.parentGroups.add(currentSubGroupWidget);
                        }
                    }
                    if (endGroup) {
                        currentGroup = null;
                        currentSubGroupWidget = null;
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}