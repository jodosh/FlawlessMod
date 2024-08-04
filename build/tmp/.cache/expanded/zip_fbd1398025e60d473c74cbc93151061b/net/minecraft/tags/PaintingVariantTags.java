package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingVariantTags {
    public static final TagKey<PaintingVariant> PLACEABLE = create("placeable");

    private PaintingVariantTags() {
    }

    private static TagKey<PaintingVariant> create(String pName) {
        return TagKey.create(Registries.PAINTING_VARIANT, ResourceLocation.withDefaultNamespace(pName));
    }

    public static TagKey<PaintingVariant> create(String namepsace, String path) {
        return create(ResourceLocation.fromNamespaceAndPath(namepsace, path));
    }

    public static TagKey<PaintingVariant> create(ResourceLocation name) {
        return TagKey.create(Registries.PAINTING_VARIANT, name);
    }
}