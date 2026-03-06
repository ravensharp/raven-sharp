package xyz.ravensharp.gc.gui.clickgui.components.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.gui.clickgui.components.Component;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class Label extends Component {
    private String text;

    public Label(String text) {
        this.text = text;
        
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        this.height = fr.FONT_HEIGHT + 4;
        setText(text);
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
    	ClickGui.drawRect(0, 0, this.width, this.height, this.isHovered(mouseX, mouseY) ? ClickGui.hover_color : ClickGui.mod_open_color);
    	ClickGui.drawRect(0, 0, 2, this.height, ClickGui.color);

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        
        fr.drawString(this.text, 6, 2, 0xFFFFFFFF);
    }

    public void setText(String text) {
        this.text = text;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        
        this.width = fr.getStringWidth(text) + 6;
    }

    public String getText() {
        return this.text;
    }
}