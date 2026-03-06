package xyz.ravensharp.gc.gui.clickgui.components.sub;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.gui.clickgui.components.Component;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class Visible extends Component {
    private Module module;
    private String displayText = "";

    public Visible(Module mod) {
        this.module = mod;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        this.height = fr.FONT_HEIGHT + 4;
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
        ClickGui.drawRect(0, 0, this.width, this.height, this.isHovered(mouseX, mouseY) ? ClickGui.hover_color : ClickGui.mod_open_color);
        ClickGui.drawRect(0, 0, 2, this.height, ClickGui.color);
        
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        
        this.displayText = this.module.getVisibility() ? "Currently Visible on Hud." : "Currently Invisible on Hud.";
        
        fr.drawString(this.displayText, 6, 2, ClickGui.text_color);

        this.width = Math.max(fr.getStringWidth(this.displayText) + 8, this.parent.width);
    }
    
    @Override
    protected boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
        	this.module.toggleVisibility();
            return true;
        }
        return false;
    }
}