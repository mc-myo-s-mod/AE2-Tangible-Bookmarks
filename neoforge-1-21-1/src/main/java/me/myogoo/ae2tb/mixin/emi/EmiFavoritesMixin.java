package me.myogoo.ae2tb.mixin.emi;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import dev.emi.emi.runtime.EmiFavorites;
import me.myogoo.ae2tb.utils.BookmarkEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EmiFavorites.class)
public class EmiFavoritesMixin {

    @Inject(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/api/stack/serializer/EmiIngredientSerializer;getDeserialized(Lcom/google/gson/JsonElement;)Ldev/emi/emi/api/stack/EmiIngredient;",
                    shift = At.Shift.AFTER
            ), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void a(JsonArray arr, CallbackInfo ci, @Local(name = "json") JsonObject json) {
//        BookmarkEventHandler.registerEvent(BookmarkEventHandler.AE2TBookmarkEvent.LOAD, () -> {
//        });
    }
}
