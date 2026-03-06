package xyz.ravensharp.gc.gui.configgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ScrollList extends GuiSlot {
    private List<String> items = new ArrayList<>();
    private int selectedIndex = -1;
    private ConfigGui parentScreen;

    public ScrollList(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn, ConfigGui parent) {
        super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
        Sharp.configManager.loadConfigInDir();
        
        this.parentScreen = parent;
        updateSlot();
    }
    
    public void updateSlot() {
        this.items = new ArrayList<>();
        for (Config conf : Sharp.configManager.getConfigs()) {
            this.items.add(conf.getName());
        }
    }

    @Override
    protected int getSize() {
        return items.size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        this.selectedIndex = slotIndex;
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == selectedIndex;
    }

    @Override
    protected void drawBackground() {
        this.parentScreen.drawDefaultBackground();
    }
    
    @Override
    public int getListWidth() {
        return 220; 
    }

    @Override
    protected int getScrollBarX() {
        return this.width / 2 + 115;
    }

    @Override
    protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
        String text = this.items.get(entryID);
        int textWidth = this.mc.fontRendererObj.getStringWidth(text);
        int textX = this.width / 2 - (textWidth / 2);
        
        this.mc.fontRendererObj.drawString(text, textX, p_180791_3_ + 4, 0xFFFFFF);
    }
    
    public String getSelectedItem() {
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            return items.get(selectedIndex);
        }
        return null;
    }

    public boolean hasSelection() {
        return selectedIndex >= 0 && selectedIndex < items.size();
    }

    public void setItems(List<String> newItems) {
        this.items = newItems;
    }
}