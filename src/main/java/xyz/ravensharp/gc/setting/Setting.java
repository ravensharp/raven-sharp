package xyz.ravensharp.gc.setting;

import java.util.ArrayList;

import xyz.ravensharp.gc.module.Module;

public class Setting {

	private SettingType settingType;
	private String settingName;
	private Integer keyCode;
	private Integer defaultKeyCode;
	private Boolean booleanValue = false;
	private Boolean defaultBooleanValue;
	private ArrayList<String> options;
	private String stringValue;
	private String defaultStringValue;
	private Double min, max, doubleValue;
	private Double defaultDoubleValue;
	private Boolean integerMode = false;
	private Module parentMod;

	private String description;

	public String getDescription() {
		return description;
	}

	public Setting setDescription(String d) {
		this.description = d;
		return this;
	}

	public void setParentMod(Module mod) {
		this.parentMod = mod;
	}

	public Module getParentMod() {
		return this.parentMod;
	}

	public SettingType getSettingType() {
		return this.settingType;
	}

	public Setting(SettingType type, int keyCode) {
		if (type.equals(SettingType.BINDING)) {
			this.settingName = type.toString();
			this.keyCode = keyCode;
			this.defaultKeyCode = keyCode;
			this.settingType = type;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: BINDING.");
		}
	}

	public Setting(SettingType type, Boolean b) {
		if (type.equals(SettingType.VISIBILITY) || type.equals(SettingType.TOGGLE)) {
			this.settingName = type.toString();
			this.booleanValue = b;
			this.defaultBooleanValue = b;
			this.settingType = type;
		} else {
			throw new IllegalArgumentException(
					"This method can only used for settings with type: VISIBILITY AND TOGGLE.");
		}
	}

	public Setting(String settingName, Boolean b) {
		this.settingType = SettingType.CHECKBOX;
		this.settingName = settingName;
		this.booleanValue = b;
		this.defaultBooleanValue = b;
	}

	public Setting(String settingName, ArrayList<String> options, String defaultValue) {
		this.settingType = SettingType.COMBO;
		this.settingName = settingName;
		this.options = options;
		this.stringValue = defaultValue;
		this.defaultStringValue = defaultValue;
	}

	public ArrayList<String> getOptions() {
		if (this.settingType.equals(SettingType.COMBO)) {
			return this.options;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: COMBO.");
		}
	}

	public Setting(String settingName, Double min, Double max, Double defaultValue) {
		this.settingType = SettingType.SLIDER;
		this.settingName = settingName;
		this.min = min;
		this.max = max;
		this.doubleValue = defaultValue;
		this.defaultDoubleValue = defaultValue;
		this.integerMode = false;
	}

	public Setting(String settingName, Double min, Double max, Double defaultValue, Boolean integerMode) {
		this.settingType = SettingType.SLIDER;
		this.settingName = settingName;
		this.min = min;
		this.max = max;
		this.doubleValue = defaultValue;
		this.defaultDoubleValue = defaultValue;
		this.integerMode = integerMode;
	}

	public void reset() {
		this.keyCode = this.defaultKeyCode;
		this.booleanValue = this.defaultBooleanValue;
		this.stringValue = this.defaultStringValue;
		this.doubleValue = this.defaultDoubleValue;
	}

	public Double getMax() {
		if (this.settingType.equals(SettingType.SLIDER)) {
			return this.max;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: SLIDER.");
		}
	}

	public Double getMin() {
		if (this.settingType.equals(SettingType.SLIDER)) {
			return this.min;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: SLIDER.");
		}
	}

	public String getSettingName() {
		return this.settingName;
	}

	public Integer getKeyCode() {
		if (this.settingType.equals(SettingType.BINDING)) {
			return this.keyCode;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: BINDING.");
		}
	}

	public Boolean getBoolean() {
		if (this.settingType.equals(SettingType.VISIBILITY) || this.settingType.equals(SettingType.CHECKBOX)
				|| this.settingType.equals(SettingType.TOGGLE)) {
			return this.booleanValue;
		} else {
			throw new IllegalArgumentException(
					"This method can only used for settings with type: VISIBILITY, CHECKBOX, TOGGLE.");
		}
	}

	public String getString() {
		if (this.settingType.equals(SettingType.COMBO)) {
			return this.stringValue;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: COMBO.");
		}
	}

	public Double getDouble() {
		if (this.settingType.equals(SettingType.SLIDER) && !this.integerMode) {
			return this.doubleValue;
		} else {
			throw new IllegalArgumentException(
					"This method can only used for settings with type: SLIDER and integerMode must be false.");
		}
	}

	public Integer getInteger() {
		if (this.settingType.equals(SettingType.SLIDER) && this.integerMode) {
			return this.doubleValue.intValue();
		} else {
			throw new IllegalArgumentException(
					"This method can only used for settings with type: SLIDER and integerMode must be true.");
		}
	}

	public void setKeyCode(Integer keyCode) {
		if (this.settingType.equals(SettingType.BINDING)) {
			this.keyCode = keyCode;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: BINDING.");
		}
	}

	public void setBoolean(Boolean b) {
		if (this.settingType.equals(SettingType.VISIBILITY) || this.settingType.equals(SettingType.CHECKBOX)
				|| this.settingType.equals(SettingType.TOGGLE)) {
			this.booleanValue = b;
		} else {
			throw new IllegalArgumentException(
					"This method can only used for settings with type: VISIBILITY, CHECKBOX, TOGGLE.");
		}
	}

	public void setString(String s) {
		if (this.settingType.equals(SettingType.COMBO)) {
			this.stringValue = s;
		} else {
			throw new IllegalArgumentException("This method can only used for settings with type: COMBO.");
		}
	}

	public void setDouble(Double d) {
		if (this.settingType.equals(SettingType.SLIDER) && !this.integerMode) {
			this.doubleValue = d;
		} else {
			throw new IllegalArgumentException(
					"This method can only used for settings with type: SLIDER and integerMode must be false.");
		}
	}

	public void setInteger(Integer i) {
		if (this.settingType.equals(SettingType.SLIDER) && this.integerMode) {
			this.doubleValue = i.doubleValue();
		} else {
			throw new IllegalArgumentException(
					"This method can only used for settings with type: SLIDER and integerMode must be true.");
		}
	}

	public boolean isSliderInteger() {
		return this.integerMode;
	}

	public float getDoubleInFloat() {
		return safeDoubleToFloat(this.getDouble());
	}

	private float safeDoubleToFloat(double value) {
		if (Double.isNaN(value)) {
			return Float.NaN;
		}
		if (value > Float.MAX_VALUE) {
			return Float.POSITIVE_INFINITY;
		}
		if (value < -Float.MAX_VALUE) {
			return Float.NEGATIVE_INFINITY;
		}
		if (value != 0 && Math.abs(value) < Float.MIN_VALUE) {
			return 0.0f;
		}

		return (float) value;
	}
}