package xyz.ravensharp.gc.gui.clickgui.components;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;

public class DescriptionElement extends Component {
	public Component target;
	private String description;

	public DescriptionElement(Component target, String description) {
		this.target = target;
		this.description = description;

		this.target.x = 0;
		this.target.y = 0;

		this.width = target.width;
		this.height = target.height;
	}

	@Override
	protected void render(int mouseX, int mouseY, float partialTicks) {
		this.target.width = this.width;

		this.target.val_is_parent_open = this.val_is_parent_open;
		this.target.handleRender(mouseX, mouseY, partialTicks);

		this.width = this.target.width;
		this.height = this.target.height;

		if (this.isHovered(mouseX, mouseY)) {
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			int textWidth = fr.getStringWidth(this.description);
			int textHeight = fr.FONT_HEIGHT;

			int tooltipX = mouseX + 8;
			int tooltipY = mouseY + 4;
			int left = tooltipX - 2;
			int top = tooltipY - 2;
			int right = tooltipX + textWidth + 2;
			int bottom = tooltipY + textHeight + 2;

			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 300.0F);

			ClickGui.drawRect(left, top, right, bottom, ClickGui.bg_color);

			fr.drawString(this.description, tooltipX, tooltipY, ClickGui.text_color);

			GL11.glPopMatrix();
		}
	}

	@Override
	protected boolean mouseClicked(int mouseX, int mouseY, int button) {
		return this.target.handleMouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected boolean mouseReleased(int mouseX, int mouseY, int button) {
		return this.target.handleMouseReleased(mouseX, mouseY, button);
	}

	@Override
	protected boolean keyTyped(char typedChar, int key) {
		return this.target.handleKeyTyped(typedChar, key);
	}
}