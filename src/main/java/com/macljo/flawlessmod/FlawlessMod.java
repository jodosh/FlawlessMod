package com.macljo.flawlessmod;

//Basic Imports
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


//Imports for Player Damage, Death Message, (Totem Canceller), Absorption Hearts
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import org.slf4j.Logger;

@Mod(FlawlessMod.MOD_ID)
public class FlawlessMod {
    public static final String MOD_ID = Config.MOD_ID;
    private static final Logger LOGGER = LogUtils.getLogger();
    //false for true hardcore (v1)
    //true for todem and grapple (v2)
    public boolean isEasyMode = true;

    public FlawlessMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }



    private void setup(final FMLCommonSetupEvent event) {
        // Pre-initialization logic
    }

    @SubscribeEvent
    public void onPlayerDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            // Check if player has absorption effect from golden apple
            boolean hasAbsorptionEffect = false;
            for (MobEffectInstance effect : player.getActiveEffects()) {
                if (effect.getEffect() == MobEffects.ABSORPTION) {
                    hasAbsorptionEffect = true;
                    break;
                }
            }

            if (isEasyMode)
            {
                // If player has absorption effect, prevent death unless absorption hearts are depleted
                if (hasAbsorptionEffect) {
                    int remainingAbsorptionHearts = calculateRemainingAbsorptionHearts(player);
                    if (remainingAbsorptionHearts > 0) {
                        event.setCanceled(true); // Cancel the death event if absorption hearts are still present
                        return; // Skip further processing if player has absorption hearts
                    }
                }
                else{
                    player.setHealth(0.0F);
                }
                // Check for Totem of Undying in inventory and remove it

                for (ItemStack itemStack : player.getInventory().items) {
                    if (itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
                        itemStack.shrink(1); // Remove one totem
                        break;
                    }
                }
            }
            else{
                player.setHealth(0.0F);
            }


        }
    }

    //@SubscribeEvent
    //public void onPlayerDeath(LivingDeathEvent event) {
    //    if (event.getEntity() instanceof Player) {
    //        Player player = (Player) event.getEntity();

            // Cancel the default death message
    //        event.setCanceled(true);

            //Send the custom death message
    //        Component customDeathMessage = Component.literal(player.getName().getString() + " missed a frame perfect trick!");
    //        player.displayClientMessage(customDeathMessage, true);
            //player.sendSystemMessage(customDeathMessage);
    //    }
    //}

    // Helper method to calculate remaining absorption hearts
    private int calculateRemainingAbsorptionHearts(Player player) {
        int maxHealth = (int) player.getMaxHealth();
        int currentHealth = (int) player.getHealth();
        int remainingAbsorptionHearts = (currentHealth - maxHealth) / 2;
        return Math.max(0, remainingAbsorptionHearts);
    }
}
