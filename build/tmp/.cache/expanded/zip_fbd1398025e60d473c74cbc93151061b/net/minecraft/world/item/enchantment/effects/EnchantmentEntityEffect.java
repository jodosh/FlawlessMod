package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public interface EnchantmentEntityEffect extends EnchantmentLocationBasedEffect {
    Codec<EnchantmentEntityEffect> CODEC = BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE.byNameCodec().dispatch(EnchantmentEntityEffect::codec, Function.identity());

    static MapCodec<? extends EnchantmentEntityEffect> bootstrap(Registry<MapCodec<? extends EnchantmentEntityEffect>> p_342629_) {
        Registry.register(p_342629_, "all_of", AllOf.EntityEffects.CODEC);
        Registry.register(p_342629_, "apply_mob_effect", ApplyMobEffect.CODEC);
        Registry.register(p_342629_, "damage_entity", DamageEntity.CODEC);
        Registry.register(p_342629_, "damage_item", DamageItem.CODEC);
        Registry.register(p_342629_, "explode", ExplodeEffect.CODEC);
        Registry.register(p_342629_, "ignite", Ignite.CODEC);
        Registry.register(p_342629_, "play_sound", PlaySoundEffect.CODEC);
        Registry.register(p_342629_, "replace_block", ReplaceBlock.CODEC);
        Registry.register(p_342629_, "replace_disk", ReplaceDisk.CODEC);
        Registry.register(p_342629_, "run_function", RunFunction.CODEC);
        Registry.register(p_342629_, "set_block_properties", SetBlockProperties.CODEC);
        Registry.register(p_342629_, "spawn_particles", SpawnParticlesEffect.CODEC);
        return Registry.register(p_342629_, "summon_entity", SummonEntityEffect.CODEC);
    }

    void apply(ServerLevel pLevel, int pEnchantmentLevel, EnchantedItemInUse pItem, Entity pEntity, Vec3 pOrigin);

    @Override
    default void onChangedBlock(ServerLevel pLevel, int pEnchantmentLevel, EnchantedItemInUse pItem, Entity pEntity, Vec3 pPos, boolean pApplyTransientEffects) {
        this.apply(pLevel, pEnchantmentLevel, pItem, pEntity, pPos);
    }

    @Override
    MapCodec<? extends EnchantmentEntityEffect> codec();
}