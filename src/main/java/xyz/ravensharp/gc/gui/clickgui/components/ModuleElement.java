package xyz.ravensharp.gc.gui.clickgui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.module.Module;

public class ModuleElement extends Component {
    private final Module mod;

    public ModuleElement(Module mod) {
        this.mod = mod;
        this.width = 120;
        this.height = 14;
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
        int bgColor = this.isHovered(mouseX, mouseY) ? ClickGui.hover_color : ClickGui.bg_color;
        
        ClickGui.drawRect(0, 0, this.width, this.height, bgColor);

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int textColor = this.mod.isToggled() ? ClickGui.color : ClickGui.text_color;
        int textWidth = fr.getStringWidth(this.mod.getName());
        
        fr.drawString(this.mod.getName(), (this.width - textWidth) / 2, (this.height - fr.FONT_HEIGHT) / 2, textColor);
    }

    @Override
    protected boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 && this.isHovered(mouseX, mouseY)) {
            this.mod.toggle();
            return true;
        }
        return false;
    }
}