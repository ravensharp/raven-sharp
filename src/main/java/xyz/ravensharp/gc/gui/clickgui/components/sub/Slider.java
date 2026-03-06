package xyz.ravensharp.gc.gui.clickgui.components.sub;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.gui.clickgui.components.Component;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class Slider extends Component {
    private Setting setting;
    private boolean isHold = false;

    public Slider(Setting set) {
        this.setting = set;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        this.height = fr.FONT_HEIGHT + 4;
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {    	
        double sliderMaxWidth = this.width * 0.95;
        double offsetX = (this.width - sliderMaxWidth) / 2.0;

        if (this.isHold) {
            double percent = (mouseX - offsetX) / sliderMaxWidth;
            percent = Math.max(0.0, Math.min(1.0, percent));
            
            double diff = this.setting.getMax() - this.setting.getMin();
            double val = this.setting.getMin() + (diff * percent);
            
            double rounded = new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
            this.setting.setDouble(rounded);
        }


        ClickGui.drawRect(0, 0, this.width, this.height, this.isHovered(mouseX, mouseY) ? ClickGui.hover_color : ClickGui.mod_open_color);
    	ClickGui.drawRect(0, 0, 2, this.height, ClickGui.color);
        
        double percent = (this.setting.getDouble() - this.setting.getMin()) / (this.setting.getMax() - this.setting.getMin());
        double sliderFillWidth = sliderMaxWidth * percent;
        
        ClickGui.drawRect(offsetX, 0, sliderMaxWidth, this.height, ClickGui.sub_bg_color);
        ClickGui.drawRect(offsetX, 0, sliderFillWidth, this.height, ClickGui.color);

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        String line = this.setting.getSettingName() + ": " + this.setting.getDouble();
        
        int textY = (this.height - fr.FONT_HEIGHT) / 2;
        fr.drawString(line, 6, textY, ClickGui.text_color);
    }

    @Override
    protected boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            this.isHold = true;
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0) {
            this.isHold = false;
            return true;
        }
        return false;
    }
}