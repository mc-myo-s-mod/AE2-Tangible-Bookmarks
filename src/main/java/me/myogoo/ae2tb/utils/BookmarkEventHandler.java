package me.myogoo.ae2tb.utils;

import java.util.LinkedList;
import java.util.Queue;

public class BookmarkEventHandler {
    public static Queue<Runnable> loadQueue = new LinkedList<>();
    public static Queue<Runnable> saveQueue = new LinkedList<>();

    public static void registerEvent(AE2TBookmarkEvent event, Runnable runnable) {
        switch (event) {
            case LOAD -> loadQueue.add(runnable);
            case SAVE -> saveQueue.add(runnable);
        }
    }

    public static void onLoad() {
        loadQueue.forEach(Runnable::run);
    }

    public static void onSave() {
        saveQueue.forEach(Runnable::run);
    }

    public enum AE2TBookmarkEvent {
        LOAD,
        SAVE
    }
}
