package me.myogoo.ae2tb.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDeserializer {
    public static List<BookmarkLayer.AE2TBBookmark> load(Path path) {
        List<BookmarkLayer.AE2TBBookmark> results = new ArrayList<>();
        if (path == null || !Files.exists(path)) {
            return results;
        }
        try (var reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                return results;
            }
            JsonArray arr = root.getAsJsonArray();
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) {
                    continue;
                }
                JsonObject obj = el.getAsJsonObject();
                long serial = obj.has("serial") ? obj.get("serial").getAsLong() : 0L;
                boolean auto = obj.has("autoCraftable") && obj.get("autoCraftable").getAsBoolean();
                ItemStack stack = ItemStack.EMPTY;
                if (obj.has("stack") && obj.get("stack").isJsonObject()) {
                    JsonObject stackObj = obj.get("stack").getAsJsonObject();
                    if (stackObj.has("nbt")) {
                        try {
                            CompoundTag tag = TagParser.parseTag(stackObj.get("nbt").getAsString());
                            stack = ItemStack.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(ItemStack.EMPTY);
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (!stack.isEmpty()) {
                    results.add(new BookmarkLayer.AE2TBBookmark(serial, auto, stack));
                }
            }
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
        return results;
    }
}
