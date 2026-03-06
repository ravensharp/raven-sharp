package xyz.ravensharp.gc.module.modules.config;

import org.lwjgl.input.Keyboard;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;

public class Panic extends Module {
	public Panic() {
		super("Panic", "Disable All Modules.", Category.CONFIG);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		for (Module mod : Sharp.moduleManager.getModules()) {
			mod.setToggled(false);
		}
	}
	
    @Override
    public void onDisable() {
    	super.onDisable();
    }
}
