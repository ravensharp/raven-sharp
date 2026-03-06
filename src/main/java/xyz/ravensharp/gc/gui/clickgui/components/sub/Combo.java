package xyz.ravensharp.gc.gui.clickgui.components.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.gui.clickgui.components.Component;
import xyz.ravensharp.gc.setting.Setting;

public class Combo extends Component {
	private Setting setting;
	private int current = 1;

	public Combo(Setting set) {
		this.setting = set;
		this.height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4;

		int i = 1;
		for (String val : set.getOptions()) {
			if (val.equals(set.getString())) {
				current = i;
				continue;
			}
			i++;
		}

		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		this.height = fr.FONT_HEIGHT + 4;
	}

	private void updateState(int btn) {
		if (btn == 0) {
			if (current + 1 > setting.getOptions().size()) {
				current = 1;
			} else
				current++;
		} else {
			if (0 >= current - 1) {
				current = setting.getOptions().size();
			} else
				current--;
		}
		this.setting.setString(setting.getOptions().get(current - 1));
	}

	@Override
	protected void render(int mouseX, int mouseY, float partialTicks) {
		ClickGui.drawRect(0, 0, this.width, this.height,
				this.isHovered(mouseX, mouseY) ? ClickGui.hover_color : ClickGui.mod_open_color);
		ClickGui.drawRect(0, 0, 2, this.height, ClickGui.color);

		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		String line = setting.getSettingName() + ": " + setting.getString();
		this.width = Math.max(fr.getStringWidth(line) + 8, this.parent.width);

		fr.drawString(line, 6, 2, ClickGui.text_color);
	}

	@Override
	protected boolean mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0 || button == 1) {
			this.updateState(button);
			return true;
		}
		return false;
	}

}