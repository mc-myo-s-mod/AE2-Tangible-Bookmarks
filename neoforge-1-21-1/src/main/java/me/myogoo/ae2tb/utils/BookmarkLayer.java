package me.myogoo.ae2tb.utils;

import appeng.api.stacks.AEItemKey;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookmarkLayer {
    public static HashMap<AEItemKey, Long> items;
    private static final List<AE2TBBookmark> BOOKMARKS = new ArrayList<>();
    private static boolean loaded = false;
    private static long nextSerial = 1L;

    public static void addBookmark(ItemStack stack, boolean autoCraftable) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        ensureLoaded();
        BOOKMARKS.add(new AE2TBBookmark(nextSerial++, autoCraftable, stack.copy()));
        BookmarkSerializer.save(getFilePath(), BOOKMARKS);
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        List<AE2TBBookmark> existing = BookmarkDeserializer.load(getFilePath());
        BOOKMARKS.addAll(existing);
        nextSerial = existing.stream().mapToLong(AE2TBBookmark::serial).max().orElse(0L) + 1L;
        loaded = true;
    }

    private static Path getFilePath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("ae2tb.json");
    }

    public record AE2TBBookmark(long serial, boolean autoCraftable, ItemStack stack) { }
}
