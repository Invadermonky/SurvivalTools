package com.invadermonky.survivaltools.blocks.subtile;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;
import vazkii.botania.common.crafting.ModPetalRecipes;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PagePetalRecipe;
import vazkii.botania.common.lexicon.page.PageText;

import java.util.List;

public class SubTileGryllzalia extends SubTileFunctional implements IAddition {
    public static LexiconEntry GRYLLZALIA_ENTRY;
    public static RecipePetals GRYLLZALIA_RECIPE;
    private static final int RANGE = 8;

    /*
        SubTileFunctional Methods
    */
    @Override
    public void onUpdate() {
        super.onUpdate();
        int delay = ConfigHandlerST.botania.gryllzalia.delay;
        int cost = ConfigHandlerST.botania.gryllzalia.cost;
        if(!this.supertile.getWorld().isRemote && this.redstoneSignal <= 0 && this.ticksExisted % delay == 0 && this.mana >= cost) {
            boolean did = false;
            List<EntityPlayer> players = this.supertile.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.supertile.getPos().add(-RANGE, -RANGE, -RANGE), this.supertile.getPos().add(RANGE,RANGE,RANGE)));
            for(EntityPlayer player : players) {
                if(SurvivalHelper.stabilizePlayerTemperature(player)) {
                    this.mana -= cost;
                    did = true;
                }
            }
            if(did) {
                this.sync();
            }
        }
    }

    @Override
    public boolean acceptsRedstone() {
        return true;
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(this.toBlockPos(), RANGE);
    }

    @Override
    public int getColor() {
        return super.getColor();
    }

    @Override
    public int getMaxMana() {
        return 100;
    }

    @Override
    public LexiconEntry getEntry() {
        return GRYLLZALIA_ENTRY;
    }

    /*
     *  IAddition
     */
    @Override
    public void preInit() {
        BotaniaAPI.addSubTileToCreativeMenu(LibNames.GRYLLZALIA);
        BotaniaAPI.registerSubTile(LibNames.GRYLLZALIA, SubTileGryllzalia.class);
    }

    @Override
    public void postInit() {
        GRYLLZALIA_RECIPE = new RecipePetals(ItemBlockSpecialFlower.ofType(LibNames.GRYLLZALIA), ModPetalRecipes.brown, ModPetalRecipes.brown, ModPetalRecipes.red, ModPetalRecipes.yellow, "runeSpringB", "redstoneRoot");
        BotaniaAPI.registerPetalRecipe(GRYLLZALIA_RECIPE.getOutput(), GRYLLZALIA_RECIPE.getInputs().toArray());
        SubTileGryllzalia.GRYLLZALIA_ENTRY = new BasicLexiconEntry(LibNames.GRYLLZALIA, BotaniaAPI.categoryFunctionalFlowers);
        SubTileGryllzalia.GRYLLZALIA_ENTRY.setLexiconPages(
                new PageText("0"),
                new PagePetalRecipe<>("1", GRYLLZALIA_RECIPE)
        );
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(ModelRegistryEvent event) {
        BotaniaAPIClient.registerSubtileModel(SubTileGryllzalia.class, new ModelResourceLocation(new ResourceLocation(SurvivalTools.MOD_ID, LibNames.GRYLLZALIA), "normal"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.botania.gryllzalia.enable && SurvivalHelper.isTemperatureFeatureEnabled();
    }
}
