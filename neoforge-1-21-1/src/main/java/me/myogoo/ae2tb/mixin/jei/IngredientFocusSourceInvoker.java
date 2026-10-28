package me.myogoo.ae2tb.mixin.jei;

import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IngredientFocusSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(IngredientFocusSource.class)
public interface IngredientFocusSourceInvoker {
    @Invoker(value = "getIngredientUnderMouse", remap = false)
    Stream<IClickableIngredientInternal<?>> ae2tb$getIngredientUnderMouse(double mouseX, double mouseY);
}
