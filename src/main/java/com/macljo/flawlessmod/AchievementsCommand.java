package com.macljo.flawlessmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = FlawlessMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AchievementsCommand {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("achievements")
                .requires(source -> source.hasPermission(0)) // Command permission level
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    int achievementsCount = getNumberOfAchievementsEarned(player);

                    // Send a message back to the player with the number of achievements
                    context.getSource().sendSuccess(() -> Component.literal("You have earned " + achievementsCount + " achievements out of " + getNumberOfAchievements(player)), true);
                    return 1;
                });

        dispatcher.register(command);
        LOGGER.info("Achievements command registered");
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LOGGER.info("Death Happened");
        if (event.getEntity() instanceof ServerPlayer player) {
            LOGGER.info("Death was player");
            event.setCanceled(true);

            Component customDeathMessage = Component.literal(getNumberOfAchievementsEarned((ServerPlayer) player) + " achievements out of " + getNumberOfAchievements((ServerPlayer) player));
            player.sendSystemMessage(customDeathMessage);

        }
    }

    private static int getNumberOfAchievementsEarned(ServerPlayer player) {
        MinecraftServer server = player.server;
        Collection<AdvancementHolder> advancements = server.getAdvancements().getAllAdvancements();
        int count = 0;

        for (AdvancementHolder advancement : advancements) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (progress.isDone()) {
                count++;
            }
        }
        return count;
    }

    private static int getNumberOfAchievements(ServerPlayer player) {
        MinecraftServer server = player.server;
        Collection<AdvancementHolder> advancements = server.getAdvancements().getAllAdvancements();
        return advancements.size();
    }
}
