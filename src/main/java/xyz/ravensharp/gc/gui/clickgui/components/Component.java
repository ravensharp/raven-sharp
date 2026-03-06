package xyz.ravensharp.gc.gui.clickgui.components;

import org.lwjgl.opengl.GL11;

public abstract class Component {
    public int x, y, width, height;
    public boolean val_is_parent_open = false;
    public ExpandableElement parent;

    public final void handleRender(int parentMouseX, int parentMouseY, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(this.x, this.y, 0);

        int relMouseX = parentMouseX - this.x;
        int relMouseY = parentMouseY - this.y;

        this.render(relMouseX, relMouseY, partialTicks);

        GL11.glPopMatrix();
    }

    public final boolean handleMouseClicked(int parentMouseX, int parentMouseY, int button) {
        int relMouseX = parentMouseX - this.x;
        int relMouseY = parentMouseY - this.y;

        if (!this.isHovered(relMouseX, relMouseY)) {
            return false;
        }

        return this.mouseClicked(relMouseX, relMouseY, button);
    }

    public final boolean handleMouseReleased(int parentMouseX, int parentMouseY, int button) {
        return this.mouseReleased(parentMouseX - this.x, parentMouseY - this.y, button);
    }

    public final boolean handleKeyTyped(char typedChar, int key) {
        return this.keyTyped(typedChar, key);
    }

    protected abstract void render(int mouseX, int mouseY, float partialTicks);

    protected boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    protected boolean mouseReleased(int mouseX, int mouseY, int button) {
        return false;
    }

    protected boolean keyTyped(char typedChar, int key) {
        return false;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= 0 && mouseX <= this.width && mouseY >= 0 && mouseY <= this.height;
    }
}