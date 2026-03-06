package xyz.ravensharp.gc.gui.clickgui.components;

import xyz.ravensharp.gc.gui.clickgui.ClickGui;

public class PaddingElement extends Component {
    private final Component target;
    private final int paddingTop;
    private final int paddingBottom;
    private final int paddingLeft;
    private final int paddingRight;
    private final int color;

    public PaddingElement(Component target, int paddingTopBottom, int paddingLeftRight, int color) {
        this(target, paddingTopBottom, paddingTopBottom, paddingLeftRight, paddingLeftRight, color);
    }

    public PaddingElement(Component target, int paddingTop, int paddingBottom, int paddingLeft, int paddingRight, int color) {
        this.target = target;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.color = color;

        this.target.x = paddingLeft;
        this.target.y = paddingTop;

        this.width = target.width + paddingLeft + paddingRight;
        this.height = target.height + paddingTop + paddingBottom;
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
        this.target.width = Math.max(this.target.width, this.width - this.paddingLeft - this.paddingRight);
        ClickGui.drawRect(0, 0, this.width, this.height, this.color);
        this.target.val_is_parent_open = this.val_is_parent_open;
        this.target.handleRender(mouseX, mouseY, partialTicks);
        this.width = Math.max(this.width, this.target.width + this.paddingLeft + this.paddingRight);
        this.height = this.target.height + this.paddingTop + this.paddingBottom;
    }

    @Override
    protected boolean mouseClicked(int mouseX, int mouseY, int button) {
        return this.target.handleMouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean mouseReleased(int mouseX, int mouseY, int button) {
        return this.target.handleMouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyTyped(char typedChar, int key) {
        return this.target.handleKeyTyped(typedChar, key);
    }
}