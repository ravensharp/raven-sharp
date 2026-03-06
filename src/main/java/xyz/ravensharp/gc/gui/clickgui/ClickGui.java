package xyz.ravensharp.gc.gui.clickgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.gui.clickgui.components.CategoryElement;
import xyz.ravensharp.gc.gui.clickgui.components.Component;
import xyz.ravensharp.gc.gui.clickgui.components.DescriptionElement;
import xyz.ravensharp.gc.gui.clickgui.components.ExpandableElement;
import xyz.ravensharp.gc.gui.clickgui.components.ModuleElement;
import xyz.ravensharp.gc.gui.clickgui.components.sub.CheckBox;
import xyz.ravensharp.gc.gui.clickgui.components.sub.Combo;
import xyz.ravensharp.gc.gui.clickgui.components.sub.Keybind;
import xyz.ravensharp.gc.gui.clickgui.components.sub.Label;
import xyz.ravensharp.gc.gui.clickgui.components.sub.Slider;
import xyz.ravensharp.gc.gui.clickgui.components.sub.Visible;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.setting.SettingType;

public class ClickGui extends GuiScreen {

	private final List<ExpandableElement> frames;

	public static final int transparent = 0x00000000;
	public static final int color = 0xFF7A4FD8;
	public static final int bg_color = 0xCC2E2E2E;
	public static final int mod_open_color = 0xCC3A3A3A;
	public static final int hover_color = 0xCC1C1C1C;
	public static final int mod_open_hover_color = 0xCC262626;
	public static final int text_color = 0xFFF0F0F0;
	public static final int sub_bg_color = 0xFF505050;

	public ClickGui() {
		this.frames = new ArrayList<>();

		int yOffset = 4;

		for (Category category : Category.values()) {
			ArrayList<Component> moduleExpElements = new ArrayList<>();
			for (Module module : Sharp.moduleManager.getModules(category)) {
				ArrayList<Component> subComponents = new ArrayList<>();
				ArrayList<Setting> settings = Sharp.settingManager.getSettings(module);

				if (settings == null || settings.isEmpty()) {
					subComponents.add(new Label("No Settings"));
				} else {
					for (Setting setting : settings) {
						Component c = null;

						if (setting.getSettingType().equals(SettingType.CHECKBOX)) {
							c = new CheckBox(setting);
						} else if (setting.getSettingType().equals(SettingType.COMBO)) {
							c = new Combo(setting);
						} else if (setting.getSettingType().equals(SettingType.SLIDER)) {
							c = new Slider(setting);
						}

						if (c == null)
							continue;

						if (setting.getDescription() != null) {
							c = new DescriptionElement(c, setting.getDescription());
						}
						subComponents.add(c);
					}
				}

				subComponents.add(new Visible(module));
				subComponents.add(new Keybind(module));

				ModuleElement moduleElement = new ModuleElement(module);
				ExpandableElement moduleExpElement = new ExpandableElement(
						new DescriptionElement(moduleElement, module.getDescription()), subComponents);
				moduleExpElements.add(moduleExpElement);
			}

			CategoryElement categoryElement = new CategoryElement(category);
			ExpandableElement categoryExpElement = new ExpandableElement(categoryElement, moduleExpElements);
			categoryExpElement.isDraggable = true;
			categoryExpElement.y = yOffset;
			yOffset += categoryExpElement.height + 4;
			categoryExpElement.x = 4;

			frames.add(categoryExpElement);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		ExpandableElement hoveredFrame = null;
		for (int i = this.frames.size() - 1; i >= 0; i--) {
			ExpandableElement frame = this.frames.get(i);
			if (mouseX >= frame.x && mouseX <= frame.x + frame.width && mouseY >= frame.y
					&& mouseY <= frame.y + frame.height) {
				hoveredFrame = frame;
				break;
			}
		}

		for (ExpandableElement frame : this.frames) {
			frame.update(mouseX, mouseY);

			int renderX = (hoveredFrame == null || frame == hoveredFrame) ? mouseX : -999;
			int renderY = (hoveredFrame == null || frame == hoveredFrame) ? mouseY : -999;

			frame.handleRender(renderX, renderY, partialTicks);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (int i = this.frames.size() - 1; i >= 0; i--) {
			ExpandableElement frame = this.frames.get(i);

			if (frame.handleMouseClicked(mouseX, mouseY, mouseButton)) {
				this.frames.remove(i);
				this.frames.add(frame);
				break;
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		for (ExpandableElement frame : this.frames) {
			frame.handleMouseReleased(mouseX, mouseY, state);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (!this.frames.isEmpty()) {
			ExpandableElement activeFrame = this.frames.get(this.frames.size() - 1);
			activeFrame.handleKeyTyped(typedChar, keyCode);
		}
	}

	public static void drawRect(double x, double y, double width, double height, int color) {
		GL11.glPushMatrix();
		net.minecraft.client.renderer.GlStateManager.enableBlend();
		net.minecraft.client.renderer.GlStateManager.disableTexture2D();
		net.minecraft.client.renderer.GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		net.minecraft.client.renderer.GlStateManager.color(f, f1, f2, f3);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x, y + height);
		GL11.glVertex2d(x + width, y + height);
		GL11.glVertex2d(x + width, y);
		GL11.glEnd();

		net.minecraft.client.renderer.GlStateManager.enableTexture2D();
		net.minecraft.client.renderer.GlStateManager.disableBlend();
		GL11.glPopMatrix();
	}
}