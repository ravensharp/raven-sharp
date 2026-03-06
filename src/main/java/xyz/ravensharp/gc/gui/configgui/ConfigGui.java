package xyz.ravensharp.gc.gui.configgui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import xyz.ravensharp.gc.Sharp;

import org.lwjgl.input.Keyboard;
import java.io.IOException;

public class ConfigGui extends GuiScreen {
    private ScrollList scrollList;
    private GuiTextField configNameField;
    private GuiButton saveButton;
    private GuiButton loadButton;
    private GuiButton overrideButton;
    private GuiButton reloadButton;
    private GuiButton deleteButton;
    private GuiButton closeButton;

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        this.scrollList = new ScrollList(this.mc, this.width, this.height, 60, this.height - 65, 20, this);
        this.scrollList.registerScrollButtons(7, 8);

        this.configNameField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 152, 25, 200, 20);
        this.configNameField.setMaxStringLength(50);
        this.configNameField.setFocused(true);

        this.saveButton = new GuiButton(0, this.width / 2 + 52, 25, 100, 20, "Save");

        this.loadButton = new GuiButton(1, this.width / 2 - 102, this.height - 52, 66, 20, "Load");
        this.overrideButton = new GuiButton(2, this.width / 2 - 33, this.height - 52, 66, 20, "Override");
        this.deleteButton = new GuiButton(3, this.width / 2 + 36, this.height - 52, 66, 20, "Delete");

        this.reloadButton = new GuiButton(4, this.width / 2 - 102, this.height - 28, 204, 20, "Reload All Configs");
        
        this.closeButton = new GuiButton(5, this.width - 55, 5, 50, 20, "Close");

        this.buttonList.add(this.saveButton);
        this.buttonList.add(this.loadButton);
        this.buttonList.add(this.overrideButton);
        this.buttonList.add(this.reloadButton);
        this.buttonList.add(this.deleteButton);
        this.buttonList.add(this.closeButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.configNameField.updateCursorCounter();

        boolean hasSelection = this.scrollList.hasSelection();
        this.loadButton.enabled = hasSelection;
        this.overrideButton.enabled = hasSelection;
        this.deleteButton.enabled = hasSelection;
        
        String typedText = this.configNameField.getText().trim();
        if (typedText.isEmpty()) {
        	this.saveButton.enabled = false;
        } else if (Sharp.configManager.getConfig(typedText) != null) {
        	this.saveButton.enabled = false;
        } else {
        	this.saveButton.enabled = true;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            String typedName = this.configNameField.getText().trim();
            if (!typedName.isEmpty() && Sharp.configManager.getConfig(typedName) == null) {
                Sharp.configManager.newConfig(typedName);
                this.scrollList.updateSlot();
            }
        } else if (button.id == 1) {
        	String selectedConfig = this.scrollList.getSelectedItem();
            if (selectedConfig != null) {
            	Sharp.configManager.getConfig(selectedConfig).loadAndApply();
            }
        } else if (button.id == 2) {
        	String selectedConfig = this.scrollList.getSelectedItem();
            if (selectedConfig != null) {
            	Sharp.configManager.getConfig(selectedConfig).saveCurrentState();
            }
        } else if (button.id == 3) {
            String selectedConfig = this.scrollList.getSelectedItem();
            if (selectedConfig != null) {
                GuiYesNo confirmDialog = new GuiYesNo(this, "Delete Config", "Are you sure you want to delete '" + selectedConfig + "'?", 3);
                this.mc.displayGuiScreen(confirmDialog);
            }
        } else if (button.id == 4) {
            Sharp.configManager.loadConfigInDir();
            this.scrollList.updateSlot();
        } else if (button.id == 5) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (result) {
            if (id == 3) {
                String selectedConfig = this.scrollList.getSelectedItem();
                if (selectedConfig != null) {
                    Sharp.configManager.getConfig(selectedConfig).delete();
                    this.scrollList.updateSlot();
                }
            }
        }
        this.mc.displayGuiScreen(this);
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.configNameField.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.configNameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollList.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.scrollList.drawScreen(mouseX, mouseY, partialTicks);
        
        this.configNameField.drawTextBox();
        
        this.drawString(this.fontRendererObj, "Config Name:", this.width / 2 - 152, 12, 0xA0A0A0);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}