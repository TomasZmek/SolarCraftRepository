package com.finderfeed.solarforge.entities;

import com.finderfeed.solarforge.misc_things.CrystalBossBuddy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class RunicWarrior extends PathfinderMob implements CrystalBossBuddy {
    public RunicWarrior(EntityType<? extends PathfinderMob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 30.0D).add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_DAMAGE,10).add(Attributes.MAX_HEALTH,50);
    }



    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 30, true, true, (l) -> true));
        this.goalSelector.addGoal(2,new MeleeAttackGoal(this,0.5,true));
        this.goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,30));
        super.registerGoals();
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }
}