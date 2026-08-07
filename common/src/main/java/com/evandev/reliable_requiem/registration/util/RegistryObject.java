package com.evandev.reliable_requiem.registration.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Represents a lazy wrapper for registry object.
 *
 * @param <T> the type of the object
 */
public interface RegistryObject<T> extends Supplier<T> {

    ResourceKey<T> getResourceKey();

    ResourceLocation getId();

    @Override
    T get();

    Holder<T> asHolder();
}
