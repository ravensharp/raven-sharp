package xyz.ravensharp.gc.gui.clickgui.components.sub;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.gui.clickgui.components.Component;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class Keybind extends Component {
    private Module module;
    private boolean isBinding = false;
    private String displayText = "";

    public Keybind(Module mod) {
        this.module = mod;
        updateState();
    }

    private void updateState() {
        if (this.isBinding) {
            this.displayText = "Click To Cancel; Waiting For Key.";
        } else {
            if (this.module.getKeyCode() == 0) {
                this.displayText = "Not Bound; Click To Bound.";
            } else {
                this.displayText = "Bound to " + Keyboard.getKeyName(this.module.getKeyCode()) + "; Click To Bound.";
            }
        }
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
        ClickGui.drawRect(0, 0, this.width, this.height, this.isHovered(mouseX, mouseY) ? ClickGui.hover_color : ClickGui.mod_open_color);
        ClickGui.drawRect(0, 0, 2, this.height, ClickGui.color);
        
        updateState();
        
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        fr.drawString(this.displayText, 6, 2, ClickGui.text_color);

        this.width = Math.max(fr.getStringWidth(this.displayText) + 8, this.parent.width);
        this.height = fr.FONT_HEIGHT + 4;
    }
    
    @Override
    protected boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            if (this.isBinding) {
                this.module.setKeyCode(0);
            }
            this.isBinding = !this.isBinding;
            this.updateState();
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean keyTyped(char typedChar, int keyCode) {
        if (this.isBinding) {
            this.module.setKeyCode(keyCode);
            this.isBinding = false;
            this.updateState();
            return true;
        }
        return false;
    }
}