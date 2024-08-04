package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * LootItemFunction that sets the LootTable and optionally the loot table seed on the stack's {@code BlockEntityTag}.
 * The effect of this is that containers such as chests will receive the given LootTable when placed.
 */
public class SetContainerLootTable extends LootItemConditionalFunction {
    public static final MapCodec<SetContainerLootTable> CODEC = RecordCodecBuilder.mapCodec(
        p_327600_ -> commonFields(p_327600_)
                .and(
                    p_327600_.group(
                        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("name").forGetter(p_327592_ -> p_327592_.name),
                        Codec.LONG.optionalFieldOf("seed", Long.valueOf(0L)).forGetter(p_297122_ -> p_297122_.seed),
                        BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter(p_297116_ -> p_297116_.type)
                    )
                )
                .apply(p_327600_, SetContainerLootTable::new)
    );
    private final ResourceKey<LootTable> name;
    private final long seed;
    private final Holder<BlockEntityType<?>> type;

    private SetContainerLootTable(List<LootItemCondition> p_297857_, ResourceKey<LootTable> p_335799_, long p_193047_, Holder<BlockEntityType<?>> p_300516_) {
        super(p_297857_);
        this.name = p_335799_;
        this.seed = p_193047_;
        this.type = p_300516_;
    }

    @Override
    public LootItemFunctionType<SetContainerLootTable> getType() {
        return LootItemFunctions.SET_LOOT_TABLE;
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        if (pStack.isEmpty()) {
            return pStack;
        } else {
            pStack.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.name, this.seed));
            return pStack;
        }
    }

    @Override
    public void validate(ValidationContext pContext) {
        super.validate(pContext);
        if (!pContext.allowsReferences()) {
            pContext.reportProblem("Uses reference to " + this.name.location() + ", but references are not allowed");
        } else {
            if (pContext.resolver().get(Registries.LOOT_TABLE, this.name).isEmpty()) {
                pContext.reportProblem("Missing loot table used for container: " + this.name.location());
            }
        }
    }

    public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> pType, ResourceKey<LootTable> pToolTable) {
        return simpleBuilder(p_327599_ -> new SetContainerLootTable(p_327599_, pToolTable, 0L, pType.builtInRegistryHolder()));
    }

    public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> pType, ResourceKey<LootTable> pLootTable, long pSeed) {
        return simpleBuilder(p_327596_ -> new SetContainerLootTable(p_327596_, pLootTable, pSeed, pType.builtInRegistryHolder()));
    }
}