package xyz.ravensharp.gc.module;

import java.util.ArrayList;

import xyz.ravensharp.gc.module.modules.combat.AimAssist;
import xyz.ravensharp.gc.module.modules.combat.Clicker;
import xyz.ravensharp.gc.module.modules.combat.JumpReset;
import xyz.ravensharp.gc.module.modules.combat.Reach;
import xyz.ravensharp.gc.module.modules.config.OpenConfigGUI;
import xyz.ravensharp.gc.module.modules.config.Panic;
import xyz.ravensharp.gc.module.modules.movement.Bridger;
import xyz.ravensharp.gc.module.modules.movement.Sprint;
import xyz.ravensharp.gc.module.modules.utility.AutoTool;
import xyz.ravensharp.gc.module.modules.utility.DelayRemover;
import xyz.ravensharp.gc.module.modules.utility.FastPlace;
import xyz.ravensharp.gc.module.modules.visual.CameraEffectRemover;
import xyz.ravensharp.gc.module.modules.visual.ClickGUI;
import xyz.ravensharp.gc.module.modules.visual.ESP;
import xyz.ravensharp.gc.module.modules.visual.Fullbright;
import xyz.ravensharp.gc.module.modules.visual.HPRenderer;
import xyz.ravensharp.gc.module.modules.visual.Hud;
import xyz.ravensharp.gc.module.modules.visual.RenderInvisibles;

public class ModuleManager {
	private ArrayList<Module> modules;

	public ModuleManager() {
		modules = new ArrayList<>();
		// Combat
		modules.add(new Clicker());
		modules.add(new AimAssist());
		modules.add(new JumpReset());
		modules.add(new Reach());
		// Movement
		modules.add(new Sprint());
		modules.add(new Bridger());
		// Visual
		modules.add(new ClickGUI());
		modules.add(new Hud());
		modules.add(new CameraEffectRemover());
		modules.add(new Fullbright());
		modules.add(new RenderInvisibles());
		modules.add(new ESP());
		modules.add(new HPRenderer());
		// Utility
		modules.add(new DelayRemover());
		modules.add(new FastPlace());
		modules.add(new AutoTool());
		// Config
		modules.add(new OpenConfigGUI());
		modules.add(new Panic());
	}

	public Module getModule(String modName) {
		for (Module mod : modules) {
			if (mod.getName().equalsIgnoreCase(modName)) {
				return mod;
			}
		}
		return null;
	}

	public ArrayList<Module> getModules() {
		return this.modules;
	}

	public ArrayList<Module> getModules(Category category) {
		ArrayList<Module> mods = new ArrayList<>();
		for (Module mod : modules) {
			if (!mod.getCategory().equals(category))
				continue;
			mods.add(mod);
		}
		return mods;
	}
}
