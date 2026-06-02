package me.myogoo.ae2tb.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BookmarkSerializer {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save(Path path, List<BookmarkLayer.AE2TBBookmark> bookmarks) {
        JsonArray array = new JsonArray();
        for (BookmarkLayer.AE2TBBookmark bookmark : bookmarks) {
            JsonObject obj = new JsonObject();
            obj.addProperty("serial", bookmark.serial());
            obj.addProperty("autoCraftable", bookmark.autoCraftable());
            obj.add("stack", stackToJson(bookmark.stack()));
            array.add(obj);
        }
        try {
            Files.createDirectories(path.getParent());
            try (var writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
                GSON.toJson(array, writer);
            }
        } catch (IOException ignored) {
        }
    }

    private static JsonObject stackToJson(ItemStack stack) {
        JsonObject obj = new JsonObject();
        Tag tag = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack).result().orElse(null);
        if (tag != null) {
            obj.addProperty("nbt", tag.toString());
        }
        return obj;
    }
}
