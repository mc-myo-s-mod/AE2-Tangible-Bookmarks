package me.myogoo.ae2tb.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AE2TBConfig {
    public static final Common COMMON = new Common();
    public static final ModConfigSpec COMMON_SPEC = COMMON.get();
    public static final Client CLIENT = new Client();
    public static final ModConfigSpec CLIENT_SPEC = CLIENT.get();

    public static boolean QoL() {
        return COMMON.QoL.get();
    }

    public static boolean enableBookmarkAmountCounting() {
        return COMMON.enableBookmarkAmountCounting.get();
    }

    public static boolean showBookmarkAmounts() {
        return CLIENT.showBookmarkAmounts.get();
    }

    public static class Common {
        private final ModConfigSpec spec;
        public final ModConfigSpec.BooleanValue QoL;
        public final ModConfigSpec.BooleanValue enableBookmarkAmountCounting;

        Common() {
            var builder = new ModConfigSpec.Builder();

            builder.push("upgrade");
            this.QoL = builder
                    .comment("Enables QoL behavior without requiring the Terminal Bookmark Interact Card. When false, bookmark interaction and bookmark amount rendering require the upgrade card in the terminal upgrade slot.")
                    .define("QoL", false);
            builder.pop();

            builder.push("performance");
            this.enableBookmarkAmountCounting = builder
                    .comment("Allows AE2TB to look up AE storage amounts for recipe-viewer bookmark entries. Disable this on the server/common config to skip the bookmark amount lookup entirely if it causes performance issues.")
                    .define("enableBookmarkAmountCounting", true);
            builder.pop();

            this.spec = builder.build();
        }

        public ModConfigSpec get() {
            return spec;
        }
    }

    public static class Client {
        private final ModConfigSpec spec;
        public final ModConfigSpec.BooleanValue showBookmarkAmounts;

        Client() {
            var builder = new ModConfigSpec.Builder();

            builder.push("display");
            this.showBookmarkAmounts = builder
                    .comment("Shows AE storage amounts on JEI/EMI/REI bookmark entries when the terminal supports bookmark amount rendering.")
                    .define("showBookmarkAmounts", true);
            builder.pop();

            this.spec = builder.build();
        }

        public ModConfigSpec get() {
            return spec;
        }
    }
}
