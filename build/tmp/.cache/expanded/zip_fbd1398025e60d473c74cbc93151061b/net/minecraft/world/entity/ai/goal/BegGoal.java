package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BegGoal extends Goal {
    private final Wolf wolf;
    @Nullable
    private Player player;
    private final Level level;
    private final float lookDistance;
    private int lookTime;
    private final TargetingConditions begTargeting;

    public BegGoal(Wolf pWolf, float pLookDistance) {
        this.wolf = pWolf;
        this.level = pWolf.level();
        this.lookDistance = pLookDistance;
        this.begTargeting = TargetingConditions.forNonCombat().range((double)pLookDistance);
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.player = this.level.getNearestPlayer(this.begTargeting, this.wolf);
        return this.player == null ? false : this.playerHoldingInteresting(this.player);
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.player.isAlive()) {
            return false;
        } else {
            return this.wolf.distanceToSqr(this.player) > (double)(this.lookDistance * this.lookDistance) ? false : this.lookTime > 0 && this.playerHoldingInteresting(this.player);
        }
    }

    @Override
    public void start() {
        this.wolf.setIsInterested(true);
        this.lookTime = this.adjustedTickDelay(40 + this.wolf.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.wolf.setIsInterested(false);
        this.player = null;
    }

    @Override
    public void tick() {
        this.wolf.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float)this.wolf.getMaxHeadXRot());
        this.lookTime--;
    }

    private boolean playerHoldingInteresting(Player pPlayer) {
        for (InteractionHand interactionhand : InteractionHand.values()) {
            ItemStack itemstack = pPlayer.getItemInHand(interactionhand);
            if (itemstack.is(Items.BONE) || this.wolf.isFood(itemstack)) {
                return true;
            }
        }

        return false;
    }
}