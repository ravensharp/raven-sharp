package xyz.ravensharp.gc.gui.clickgui.components.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.gui.clickgui.components.Component;
import xyz.ravensharp.gc.setting.Setting;

public class CheckBox extends Component {
	private Setting setting;
	private boolean isClicked = false;

	public CheckBox(Setting set) {
		this.setting = set;
		this.height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4;
		this.updateState();
		isClicked = set.getBoolean();
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		this.height = fr.FONT_HEIGHT + 4;
	}

	private void updateState() {
		isClicked = !isClicked;
		setting.setBoolean(isClicked);
	}

	@Override
	protected void render(int mouseX, int mouseY, float partialTicks) {
		ClickGui.drawRect(0, 0, this.width, this.height,
				this.isHovered(mouseX, mouseY) ? ClickGui.hover_color : ClickGui.mod_open_color);
		ClickGui.drawRect(0, 0, 2, this.height, ClickGui.color);

		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		String line = setting.getSettingName();
		this.width = Math.max(fr.getStringWidth(line) + 14, this.parent.width);

		double ySize = 4;
		double yPlace = (this.height / 2) - (ySize / 2) - 1;

		ClickGui.drawRect(4, yPlace, yPlace + ySize, yPlace + ySize,
				setting.getBoolean() ? ClickGui.color : ClickGui.sub_bg_color);

		fr.drawString(line, 14, 2, ClickGui.text_color);
	}

	@Override
	protected boolean mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0) {
			this.updateState();
			return true;
		}
		return false;
	}

}