package me.myogoo.ae2tb.gametest;

import appeng.api.integrations.rei.IngredientConverter;
import appeng.api.integrations.rei.IngredientConverters;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.rei.FluidIngredientConverter;
import appeng.integration.modules.rei.ItemIngredientConverter;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.runtime.DefaultRuntimePlugin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

final class ReiKeyConversionClientProbe {
    static void run() {
        var entryTypes = EntryTypeRegistry.getInstance();
        if (!entryTypes.keySet().contains(VanillaEntryTypes.ITEM.getId())) {
            new DefaultRuntimePlugin().registerEntryTypes(entryTypes);
        }
        try {
            Method convert = Arrays.stream(Class.forName("me.shedaniel.rei.impl.client.gui.widget.region.RegionEntryWidget").getDeclaredMethods())
                    .filter(method -> method.getName().contains("ae2tb$toGenericStack"))
                    .findFirst().orElseThrow(() -> new AssertionError("REI key conversion mixin is missing"));
            convert.setAccessible(true);

            var itemConverter = new ItemIngredientConverter();
            IngredientConverter<?> fluidConverter = new FluidIngredientConverter();
            IngredientConverters.register(itemConverter);
            IngredientConverters.register(fluidConverter);
            var item = new GenericStack(AEItemKey.of(Items.STONE), 3);
            assertEquals(item, convert.invoke(null, itemConverter.getIngredientFromStack(item)),
                    "Item key and amount must be preserved");

            var taggedFluid = new FluidStack(Fluids.WATER, 1250);
            taggedFluid.set(DataComponents.CUSTOM_NAME, Component.literal("AE2TB fluid identity"));
            var fluidKey = AEFluidKey.of(taggedFluid);
            var fluid = new GenericStack(fluidKey, 1250);
            assertEquals(fluid, convert.invoke(null, fluidConverter.getIngredientFromStack(fluid)),
                    "Fluid identity, metadata and amount must survive conversion instead of using a bucket item");

            EntryType<String> customType = EntryType.deferred(me.myogoo.ae2tb.AE2TB.makeId("probe_custom_key"));
            var custom = new GenericStack(AEFluidKey.of(Fluids.LAVA), 42);
            IngredientConverters.register(new IngredientConverter<String>() {
                @Override
                public EntryType<String> getIngredientType() {
                    return customType;
                }

                @Override
                public EntryStack<String> getIngredientFromStack(GenericStack stack) {
                    return null;
                }

                @Override
                public GenericStack getStackFromIngredient(EntryStack<String> ingredient) {
                    return "accepted".equals(ingredient.getValue()) ? custom : null;
                }
            });
            assertEquals(custom, convert.invoke(null, customEntry(customType, "accepted")),
                    "Custom converter key must win over the cheat item");
            assertEquals(null, convert.invoke(null, customEntry(customType, "rejected")),
                    "Converter refusal must not fall back to the cheat item");

            EntryType<String> unknownType = EntryType.deferred(me.myogoo.ae2tb.AE2TB.makeId("probe_unmapped_key"));
            assertEquals(new GenericStack(AEItemKey.of(Items.STONE), 1),
                    convert.invoke(null, customEntry(unknownType, "accepted")),
                    "Unmapped entries must retain the existing cheat-item fallback");
            assertEquals(null, convert.invoke(null, new Object[]{null}), "Null entries must be ignored");
            assertEquals(null, convert.invoke(null, EntryStack.empty()), "Empty entries must be ignored");
            System.out.println("AE2TB REI key conversion checks PASS");
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Unable to exercise transformed REI key conversion", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static EntryStack<String> customEntry(EntryType<String> type, String value) {
        return (EntryStack<String>) Proxy.newProxyInstance(EntryStack.class.getClassLoader(),
                new Class<?>[]{EntryStack.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getType" -> type;
                    case "isEmpty" -> false;
                    case "getValue", "castValue" -> value;
                    case "getValueType" -> String.class;
                    case "cheatsAs" -> EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(Items.STONE));
                    default -> throw new AssertionError("Unexpected entry access: " + method.getName());
                });
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(message + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
