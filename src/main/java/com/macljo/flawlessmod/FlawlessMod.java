package com.macljo.flawlessmod;

//Basic Imports
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
import net.minecraftforge.event.ServerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod(FlawlessMod.MOD_ID)
public class FlawlessMod {
    public static final String MOD_ID = Config.MOD_ID;
    private static final Logger LOGGER = LogUtils.getLogger();
    //false for true hardcore (v1)
    //true for todem and grapple (v2)
    public boolean isEasyMode = true;

    public FlawlessMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        //MinecraftForge.EVENT_BUS.register(this);
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

/*
    @SubscribeEvent
    public static void onPlayerChat(@NotNull ServerChatEvent event) {
        if (event.getPlayer() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getPlayer();
            int achievementsCount = getNumberOfAchievements(player);

            // Send a message back to the player with the number of achievements
            player.sendSystemMessage(Component.literal("You have earned " + achievementsCount + " achievements."));
        }
    }

    private static int getNumberOfAchievements(ServerPlayer player) {
        // Access the player's achievements and count them
        return 5;
    }

 */

}
