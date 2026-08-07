package com.evandev.reliable_requiem.registration.util;

import com.evandev.reliable_requiem.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Utility interface for multiloader registration.
 *
 * @param <T> the type of the objects that this class registers
 */
public interface RegistrationProvider<T> {

    static <T> RegistrationProvider<T> get(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        return Factory.INSTANCE.create(resourceKey, modId);
    }

    static <T> RegistrationProvider<T> get(Registry<T> registry, String modId) {
        return Factory.INSTANCE.create(registry, modId);
    }

    <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier);

    Collection<RegistryObject<T>> getEntries();

    String getModId();

    interface Factory {

        Factory INSTANCE = Services.load(Factory.class);

        <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId);

        default <T> RegistrationProvider<T> create(Registry<T> registry, String modId) {
            return create(registry.key(), modId);
        }
    }
}
