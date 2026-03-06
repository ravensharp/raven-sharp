package xyz.ravensharp.gc.gui.clickgui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.module.Category;

public class CategoryElement extends Component {
    private final String categoryName;

    public CategoryElement(Category category) {
        String name = category.name();
        this.categoryName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        
        this.width = 120;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        this.height = fr.FONT_HEIGHT + 4;
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
        ClickGui.drawRect(0, 0, this.width, this.height, ClickGui.color);
        
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int textWidth = fr.getStringWidth(this.categoryName);
        
        float textX = (this.width - textWidth) / 2.0f;
        float textY = 2;

        fr.drawString(this.categoryName, (int)textX, (int)textY, ClickGui.text_color);
        
        String isOpenIndicator = this.val_is_parent_open ? "-" : "+";
        fr.drawString(isOpenIndicator, this.width - fr.getStringWidth(isOpenIndicator) - 2, (int)textY, ClickGui.text_color);
        
    }
}