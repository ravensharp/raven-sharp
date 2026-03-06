package xyz.ravensharp.gc.module.modules.config;

import org.lwjgl.input.Keyboard;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;

public class OpenConfigGUI extends Module {
	public OpenConfigGUI() {
		super("Open Config Manager", "Open Config Manager.", Category.CONFIG);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		mc.displayGuiScreen(Sharp.configGui);
		this.setToggled(false);
	}
	
    @Override
    public void onDisable() {
    	super.onDisable();
    }
}
