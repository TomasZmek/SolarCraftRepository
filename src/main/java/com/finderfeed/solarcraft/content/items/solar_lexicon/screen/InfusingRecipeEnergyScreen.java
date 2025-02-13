package com.finderfeed.solarcraft.content.items.solar_lexicon.screen;

import com.finderfeed.solarcraft.SolarCraft;
import com.finderfeed.solarcraft.content.items.solar_lexicon.screen.buttons.ItemStackTabButton;
import com.finderfeed.solarcraft.helpers.ClientHelpers;
import com.finderfeed.solarcraft.local_library.helpers.RenderingTools;
import com.finderfeed.solarcraft.content.items.solar_lexicon.SolarLexicon;
import com.finderfeed.solarcraft.misc_things.RunicEnergy;

import com.finderfeed.solarcraft.content.recipe_types.infusing_new.InfusingRecipe;
import com.finderfeed.solarcraft.registries.items.SolarcraftItems;
import com.finderfeed.solarcraft.registries.sounds.SolarcraftSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class InfusingRecipeEnergyScreen extends Screen {
    public final ResourceLocation BUTTONS = new ResourceLocation("solarcraft","textures/misc/page_buttons.png");
    public final ResourceLocation MAIN_SCREEN_OPENED = new ResourceLocation("solarcraft","textures/gui/runic_and_solar_energy_costs.png");
    private int currentPage = 0;
    private final int maxPages;
    private final List<InfusingRecipe> recipes;
    private final RunicEnergy.Type[] ORDERED_TYPES = {
      RunicEnergy.Type.GIRO, RunicEnergy.Type.ULTIMA, RunicEnergy.Type.ZETA, RunicEnergy.Type.ARDO, RunicEnergy.Type.TERA, RunicEnergy.Type.FIRA, RunicEnergy.Type.URBA
      , RunicEnergy.Type.KELDA
    };
    private int relX;
    private int relY;
    private List<Runnable> postRender = new ArrayList<>();

    public InfusingRecipeEnergyScreen(InfusingRecipe recipe) {
        super(Component.literal(""));
        this.recipes = List.of(recipe);
        maxPages = 0;
    }

    public InfusingRecipeEnergyScreen(List<InfusingRecipe> recipes) {
        super(Component.literal(""));
        this.recipes = recipes;
        maxPages = recipes.size()-1;
    }


    public InfusingRecipeEnergyScreen(List<InfusingRecipe> recipes,int currentPage) {
        super(Component.literal(""));
        this.recipes = recipes;
        maxPages = recipes.size()-1;
        this.currentPage = currentPage;
    }

    @Override
    protected void init() {
        super.init();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        int scale = (int) minecraft.getWindow().getGuiScale();
        this.relX = (width/scale - 183)/2 - 12;
        this.relY = (height - 218*scale)/2/scale;



        if (maxPages != 0) {
            addRenderableWidget(new ImageButton(relX + 193 + 19, relY + 55 + 13, 16, 16, 0, 0, 0, BUTTONS, 16, 32, (button) -> {
                if ((currentPage + 1 <= maxPages)) {
                    currentPage += 1;

                }
            },(button,matrices,mousex,mousey)->{
                renderTooltip(matrices,Component.literal("Next recipe"),mousex,mousey);
            },Component.literal("")){
                @Override
                public void playDownSound(SoundManager manager) {
                    manager.play(SimpleSoundInstance.forUI(SolarcraftSounds.BUTTON_PRESS2.get(),1,1));
                }
            });
            addRenderableWidget(new ImageButton(relX + 193 + 19, relY + 55 + 13 + 16, 16, 16, 0, 16, 0, BUTTONS, 16, 32, (button) -> {
                if ((currentPage - 1 >= 0)) {
                    currentPage -= 1;

                }
            },(button,matrices,mousex,mousey)->{
                renderTooltip(matrices,Component.literal("Previous recipe"),mousex,mousey);
            },Component.literal("")){
                @Override
                public void playDownSound(SoundManager manager) {
                    manager.play(SimpleSoundInstance.forUI(SolarcraftSounds.BUTTON_PRESS2.get(),1,1));
                }
            });
        }


        int xoffs = 211;
        addRenderableWidget(new ItemStackTabButton(relX+xoffs,relY+9 + 6,17,17,(button)->{minecraft.setScreen(new InfusingRecipeScreen(recipes,currentPage));}, SolarcraftItems.INFUSER_ITEM.get().getDefaultInstance(),0.7f));
        addRenderableWidget(new ItemStackTabButton(relX+xoffs,relY+27 + 6,17,17,(button)->{minecraft.setScreen(new SolarLexiconRecipesScreen());}, Items.CRAFTING_TABLE.getDefaultInstance(),0.7f));
        addRenderableWidget(new ItemStackTabButton(relX+xoffs,relY+27 + 6 + 18,17,17,(button)->{
            Minecraft mc = Minecraft.getInstance();
            SolarLexicon lexicon = (SolarLexicon) mc.player.getMainHandItem().getItem();
            lexicon.currentSavedScreen = this;
            minecraft.setScreen(null);
        }, Items.WRITABLE_BOOK.getDefaultInstance(),0.7f));
    }



    @Override
    public void render(PoseStack matrices, int mousex, int mousey, float partialTicks) {
        InfusingRecipe recipe = recipes.get(currentPage);
        int xoffs = 1;
        matrices.pushPose();
        ClientHelpers.bindText(MAIN_SCREEN_OPENED);
        blit(matrices,relX,relY,0,0,256,256);
        matrices.popPose();

        //float percent = (float)recipe.get(currentPage).requriedEnergy / 100000;
        matrices.pushPose();
        drawCenteredString(matrices,font,Component.translatable("solarcraft.total_energy"),relX+102 + xoffs,relY+126,SolarLexiconScreen.TEXT_COLOR);
        matrices.popPose();
        matrices.pushPose();
        int iter = 0;

        for (RunicEnergy.Type type : ORDERED_TYPES){
            renderEnergyBar(matrices,relX+64+iter*17 + xoffs,relY+90,type,recipe,mousex,mousey);
            iter++;
        }
        int solaren = Math.round((float)recipe.requriedEnergy / 100000 * 63);
        fill(matrices,relX+15 + xoffs,relY+93-solaren,relX+25 + xoffs,relY+93,0xddffff00);
        if (RenderingTools.isMouseInBorders(mousex,mousey,relX + 17,relY + 31,relX + 17 + 10,relY + 93)){
            postRender.add(()->{
               renderTooltip(matrices,Component.literal(String.valueOf(recipe.requriedEnergy)),mousex,mousey);
            });
        }
        double totalEnergy = recipe.requriedEnergy;
        for (double cost : recipe.RUNIC_ENERGY_COST.getCosts()){
            totalEnergy+=cost;
        }
        int totaltext = Math.round((float)totalEnergy / 900000 * 173);
        fill(matrices,relX+16 + xoffs,relY+145,relX+16+totaltext + xoffs,relY+145+6,0xddffff00);
        drawString(matrices,font,Component.translatable("solarcraft.total_solar_energy"),relX+16 + xoffs,relY+160,SolarLexiconScreen.TEXT_COLOR);
        drawCenteredString(matrices,font,""+recipe.requriedEnergy,relX+160 + xoffs,relY+161,SolarLexiconScreen.TEXT_COLOR);

        drawString(matrices,font,Component.translatable("solarcraft.total_runic_energy"),relX+16 + xoffs,relY+160+21,SolarLexiconScreen.TEXT_COLOR);
        drawCenteredString(matrices,font,""+(int)(totalEnergy-recipe.requriedEnergy),relX+160 + xoffs,relY+161+21,SolarLexiconScreen.TEXT_COLOR);
        matrices.popPose();
        super.render(matrices, mousex, mousey, partialTicks);
        postRender.forEach(Runnable::run);
        postRender.clear();
    }


    private void renderEnergyBar(PoseStack matrices, int offsetx, int offsety, RunicEnergy.Type type,InfusingRecipe recipe,int mx,int my){
        matrices.pushPose();
        float energyCostPerItem = recipe.RUNIC_ENERGY_COST.get(type);
        int xtexture =  ( Math.round( (float)energyCostPerItem/100000*60));
        fill(matrices,offsetx,offsety-xtexture,offsetx+6,offsety,0xddffff00);
        if (RenderingTools.isMouseInBorders(mx,my,offsetx,offsety - 60,offsetx + 6,offsety)){
            postRender.add(()->{
               renderTooltip(matrices,Component.literal(String.valueOf(energyCostPerItem)),mx,my);
            });
        }
        matrices.popPose();
    }
}
