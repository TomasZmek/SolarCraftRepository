package com.finderfeed.solarcraft.content.items.solar_wand.client;

import com.finderfeed.solarcraft.SolarcraftClientInit;
import com.finderfeed.solarcraft.content.items.solar_wand.SolarWandItem;
import com.finderfeed.solarcraft.content.items.solar_wand.WandAction;
import com.finderfeed.solarcraft.local_library.client.screens.DefaultScreen;
import com.finderfeed.solarcraft.local_library.client.screens.RadialMenu;
import com.finderfeed.solarcraft.local_library.helpers.RenderingTools;
import com.finderfeed.solarcraft.packet_handler.SolarCraftPacketHandler;
import com.finderfeed.solarcraft.packet_handler.packets.CastWandActionPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class WandModeSelectionScreen extends DefaultScreen {

    private RadialMenu menu;

    public WandModeSelectionScreen(){

    }

    @Override
    protected void init() {
        super.init();
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        RadialMenu.RadialMenuShaderSettings settings =
                new RadialMenu.RadialMenuShaderSettings(0,0.5f,0.1f,
                        new float[]{0.6f,0.6f,0.6f,0.8f},
                        new float[]{0.9f,0.9f,0.9f,0.8f},
                        0.05f
                        );
        List<RadialMenu.RadialMenuSection> sections = new ArrayList<>();

        for (WandAction<?> action : SolarWandItem.getAllActions()){
            RadialMenu.RadialMenuSection section = new RadialMenu.RadialMenuSection(
                    ()->{
                        SolarCraftPacketHandler.INSTANCE.sendToServer(new CastWandActionPacket(action.getRegistryName()));
                    },
                    (matrices, x, y) -> {
                        RenderingTools.renderScaledGuiItemCentered(action.getIcon(),x,y,1,10);
                    },
                    (matrices, x, y) -> {
                        renderTooltip(matrices,minecraft.font.split(action.getActionDescription(),
                                (int)Math.min(200,window.getGuiScaledWidth() - x)),(int)x,(int)y);
                    }
            );
            section.setPressable(false);
            sections.add(section);
        }
        RadialMenu menu = new RadialMenu(settings,relX,relY,100,
                sections
        );
        this.menu = menu;
    }

    private int mx;
    private int my;

    @Override
    public void render(PoseStack matrices, int mx, int my, float pTicks) {
        super.render(matrices, mx, my, pTicks);
        this.mx = mx;
        this.my = my;
        menu.render(matrices,mx,my,pTicks,0);
    }

    @Override
    public boolean mouseClicked(double x, double y, int p_94697_) {
        menu.mouseClicked((float)x,(float)y);
        return super.mouseClicked(x, y, p_94697_);
    }

    @Override
    public boolean keyPressed(int keyCode, int idk, int type) {
        return super.keyPressed(keyCode, idk, type);
    }

    @Override
    public boolean keyReleased(int keyCode, int p_94716_, int p_94717_) {
        InputConstants.Key key = SolarcraftClientInit.GUI_WAND_MODE_SELECTION.getKey();
        int code = key.getValue();
        if (keyCode == code) {
            RadialMenu.RadialMenuSection section = menu.getSection(menu.getSectionUnderMouse(mx, my));
            if (section != null) {
                section.onPress.run();
            }
            Minecraft.getInstance().setScreen(null);
        }
        return super.keyReleased(keyCode, p_94716_, p_94717_);
    }


    @Override
    public int getScreenWidth() {
        return 0;
    }

    @Override
    public int getScreenHeight() {
        return 0;
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
