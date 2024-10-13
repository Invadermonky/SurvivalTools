package com.invadermonky.survivaltools.compat.bloodmagic;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.ritual.*;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

@RitualRegister(value = LibNames.RITUAL_SOOTHING_HEARTH)
public class RitualSoothingHearth extends Ritual {
    public static final String TEMP_CONTROL_RANGE = "tempControlRange";
    private final int refreshCost;
    private final int refreshInterval;

    public RitualSoothingHearth() {
        super(LibNames.RITUAL_SOOTHING_HEARTH, 0, ConfigHandlerST.blood_magic.soothing_hearth.activationCost, StringHelper.getTranslationKey(LibNames.RITUAL_SOOTHING_HEARTH, "ritual"));
        this.addBlockRange(TEMP_CONTROL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-25, 0, -25), new BlockPos(25, 30, 25)));
        this.setMaximumVolumeAndDistanceOfRange(TEMP_CONTROL_RANGE, 0, 200, 200);
        this.refreshCost = ConfigHandlerST.blood_magic.soothing_hearth.refreshCost;
        this.refreshInterval = ConfigHandlerST.blood_magic.soothing_hearth.refreshInterval;
    }


    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();
        BlockPos pos = masterRitualStone.getBlockPos();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if(currentEssence < this.getRefreshCost()) {
            masterRitualStone.getOwnerNetwork().causeNausea();
        } else {
            int maxEffects = currentEssence / this.getRefreshCost();
            int totalEffects = 0;
            if(masterRitualStone.getCooldown() > 0) {

            }

            AreaDescriptor tempControlRange = masterRitualStone.getBlockRange(TEMP_CONTROL_RANGE);
            AxisAlignedBB tempControlBB = tempControlRange.getAABB(masterRitualStone.getBlockPos());
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, tempControlBB);


            for(EntityPlayer player : players) {
                if(player.isCreative())
                    continue;

                if(totalEffects >= maxEffects)
                    break;

                if(SurvivalHelper.stabilizePlayerTemperature(player))
                    totalEffects++;
            }

            masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost() * totalEffects));
        }
    }

    @Override
    public int getRefreshTime() {
        return this.refreshInterval;
    }

    @Override
    public int getRefreshCost() {
        return this.refreshCost;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addParallelRunes(components, 1, 0, EnumRuneType.EARTH);
        this.addCornerRunes(components, 2, 0, EnumRuneType.WATER);
        this.addParallelRunes(components, 2, 1, EnumRuneType.AIR);
        this.addCornerRunes(components, 2, 1, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSoothingHearth();
    }

    public static boolean isEnabled() {
        return BloodMagic.RITUAL_MANAGER.getConfig().getBoolean(LibNames.RITUAL_SOOTHING_HEARTH, "rituals", true, "Enable the " + LibNames.RITUAL_SOOTHING_HEARTH + " ritual.");
    }
}
