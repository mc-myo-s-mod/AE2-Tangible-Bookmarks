package me.myogoo.ae2tb.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class AE2TBConfig {
    public static final Common COMMON = new Common();
    public static final ForgeConfigSpec COMMON_SPEC = COMMON.get();
    public static final Client CLIENT = new Client();
    public static final ForgeConfigSpec CLIENT_SPEC = CLIENT.get();

    private AE2TBConfig() {
    }

    public static boolean QoL() {
        return COMMON.QoL.get();
    }

    public static boolean enableBookmarkAmountCounting() {
        return COMMON.enableBookmarkAmountCounting.get();
    }

    public static boolean showBookmarkAmounts() {
        return CLIENT.showBookmarkAmounts.get();
    }

    public static final class Common {
        private final ForgeConfigSpec spec;
        public final ForgeConfigSpec.BooleanValue QoL;
        public final ForgeConfigSpec.BooleanValue enableBookmarkAmountCounting;

        private Common() {
            var builder = new ForgeConfigSpec.Builder();

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

        public ForgeConfigSpec get() {
            return spec;
        }
    }

    public static final class Client {
        private final ForgeConfigSpec spec;
        public final ForgeConfigSpec.BooleanValue showBookmarkAmounts;

        private Client() {
            var builder = new ForgeConfigSpec.Builder();

            builder.push("display");
            this.showBookmarkAmounts = builder
                    .comment("Shows AE storage amounts on JEI/EMI/REI bookmark entries when the terminal supports bookmark amount rendering.")
                    .define("showBookmarkAmounts", true);
            builder.pop();

            this.spec = builder.build();
        }

        public ForgeConfigSpec get() {
            return spec;
        }
    }
}
