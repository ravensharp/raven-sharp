package xyz.ravensharp.gc.module.modules.visual;

import org.lwjgl.input.Keyboard;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;

public class ClickGUI extends Module {
	public ClickGUI() {
		super("ClickGUI", "Toggle and configure modules.", Category.VISUAL);
		this.setKeyIfUnbound(Keyboard.KEY_RSHIFT);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		mc.displayGuiScreen(Sharp.clickGui);
		this.setToggled(false);
	}
	
    @Override
    public void onDisable() {
    	super.onDisable();
    }

}
