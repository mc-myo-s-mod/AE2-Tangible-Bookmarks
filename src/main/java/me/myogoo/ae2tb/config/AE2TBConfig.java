package me.myogoo.ae2tb.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class AE2TBConfig {
    public static final Common COMMON = new Common();
    public static final ForgeConfigSpec COMMON_SPEC = COMMON.get();
    public static final ForgeConfigSpec CLIENT_SPEC = new ForgeConfigSpec.Builder().build();

    private AE2TBConfig() {
    }

    public static boolean allowBookmarkInteractionWithoutUpgrade() {
        return COMMON.allowBookmarkInteractionWithoutUpgrade.get();
    }

    public static final class Common {
        private final ForgeConfigSpec spec;
        public final ForgeConfigSpec.BooleanValue allowBookmarkInteractionWithoutUpgrade;

        private Common() {
            var builder = new ForgeConfigSpec.Builder();

            builder.push("upgrade");
            this.allowBookmarkInteractionWithoutUpgrade = builder
                    .comment("Allows bookmark interaction without installing the Terminal Bookmark Interact Card.")
                    .define("allowBookmarkInteractionWithoutUpgrade", false);
            builder.pop();

            this.spec = builder.build();
        }

        public ForgeConfigSpec get() {
            return spec;
        }
    }
}
