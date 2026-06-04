package me.myogoo.ae2tb.mixin.emi;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import dev.emi.emi.com.unascribed.qdcss.QDCSS;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiLog;
import me.myogoo.ae2tb.integration.emi.AE2TBEmiBind;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(EmiConfig.class)
public class EmiConfigMixin {
    @Shadow
    private static void assignField(QDCSS css, String key, Field field) {
    }

    @Shadow
    private static String writeField(String key, Field field) {
        return null;
    }

    @Inject(method = "loadConfig(Ldev/emi/emi/com/unascribed/qdcss/QDCSS;)V", at = @At(value = "INVOKE", target = "Ldev/emi/emi/com/unascribed/qdcss/QDCSS;keySet()Ljava/util/Set;", shift = At.Shift.BEFORE), remap = false, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void addConfigDefaults(QDCSS css, CallbackInfo ci, @Local(name = "consumed") Set<String> consumed) {
        for (Field field : AE2TBEmiBind.class.getFields()) {
            EmiConfig.ConfigValue annot = field.getAnnotation(EmiConfig.ConfigValue.class);
            if (annot != null) {
                if (css.containsKey(annot.value())) {
                    consumed.add(annot.value());
                    assignField(css, annot.value(), field);
                }
            }
        }
    }

    @Inject(method = "getSavedConfig", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0, shift = At.Shift.BEFORE), remap = false, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void addConfigEntries(CallbackInfoReturnable<String> cir,
            @Local(name = "unparsed") Map<String, List<String>> unparsed,
            @Local(name = "wrapper") StringSplitter wrapper) {
        for (Field field : AE2TBEmiBind.class.getFields()) {
            EmiConfig.ConfigValue annot = field.getAnnotation(EmiConfig.ConfigValue.class);
            if (annot != null) {
                String[] parts = annot.value().split("\\.");
                String group = parts[0];
                String key = parts[1];
                EmiConfig.Comment comment = field.getAnnotation(EmiConfig.Comment.class);
                String commentText = "";
                if (comment != null) {
                    commentText += "\t/**\n";
                    for (FormattedText line : wrapper.splitLines(comment.value(), 80, Style.EMPTY)) {
                        commentText += "\t * ";
                        commentText += line.getString();
                        commentText += "\n";
                    }
                    commentText += "\t */\n";
                }
                String text = commentText;
                try {
                    text += writeField(key, field);
                } catch (Exception e) {
                    EmiLog.error("Error serializing config", e);
                }
                unparsed.computeIfAbsent(group, g -> Lists.newArrayList()).add(text);
            }
        }
    }
}
