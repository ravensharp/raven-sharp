package xyz.ravensharp.gc.gui.clickgui.components;

import java.util.List;

public class ExpandableElement extends Component {
	public Component primary;
	public List<Component> expandables;

	public boolean isDraggable = false;
	private boolean open = false;

	private boolean dragging = false;
	private int dragX, dragY;

	public ExpandableElement(Component primary, List<Component> expandables) {
		this.primary = primary;
		this.expandables = expandables;
		this.width = primary.width;
		this.height = primary.height;

		this.primary.x = 0;
		this.primary.y = 0;
		this.primary.parent = this;

		if (this.expandables != null) {
			for (Component child : this.expandables) {
				child.parent = this;
				if (child instanceof DescriptionElement) {
					DescriptionElement de = (DescriptionElement) child;
					de.target.parent = this;
				}
			}
		}
	}

	public void open() {
		this.open = true;
	}

	public void close() {
		this.open = false;
	}

	public boolean isOpen() {
		return this.open;
	}

	public void update(int absoluteMouseX, int absoluteMouseY) {
		if (this.dragging && this.isDraggable) {
			this.x = absoluteMouseX - this.dragX;
			this.y = absoluteMouseY - this.dragY;
		}

		int currentY = this.primary.height;
		int maxWidth = this.primary.width;

		if (this.expandables != null) {
			for (Component child : this.expandables) {
				if (child instanceof ExpandableElement) {
					((ExpandableElement) child).update(0, 0);
				}

				if (child.width > maxWidth) {
					maxWidth = child.width;
				}
			}
		}

		this.primary.width = maxWidth;

		if (this.open && this.expandables != null) {
			for (Component child : this.expandables) {
				child.x = 0;
				child.y = currentY;

				if (child instanceof ExpandableElement) {
					((ExpandableElement) child).primary.width = maxWidth;
				}

				child.width = maxWidth;
				currentY += child.height;
			}
		}

		this.width = maxWidth;
		this.height = currentY;
	}

	@Override
	protected void render(int mouseX, int mouseY, float partialTicks) {
		this.primary.val_is_parent_open = this.open;
		this.primary.handleRender(mouseX, mouseY, partialTicks);

		if (this.open && this.expandables != null) {
			for (Component child : this.expandables) {
				child.handleRender(mouseX, mouseY, partialTicks);
			}
		}
	}

	@Override
	protected boolean mouseClicked(int mouseX, int mouseY, int button) {
		if (this.open && this.expandables != null) {
			for (Component child : this.expandables) {
				if (child.handleMouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}
		}

		if (this.primary.isHovered(mouseX, mouseY)) {
			if (button == 1) {
				this.open = !this.open;
			} else if (button == 0 && this.isDraggable) {
				this.dragging = true;
				this.dragX = mouseX;
				this.dragY = mouseY;
			}
			this.primary.handleMouseClicked(mouseX, mouseY, button);
			return true;
		}
		return false;
	}

	@Override
	protected boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (button == 0) {
			this.dragging = false;
		}

		boolean handled = this.primary.handleMouseReleased(mouseX, mouseY, button);

		if (this.open && this.expandables != null) {
			for (Component child : this.expandables) {
				if (child.handleMouseReleased(mouseX, mouseY, button)) {
					handled = true;
				}
			}
		}
		return handled;
	}

	@Override
	protected boolean keyTyped(char typedChar, int key) {
		if (this.open && this.expandables != null) {
			for (Component child : this.expandables) {
				if (child.handleKeyTyped(typedChar, key)) {
					return true;
				}
			}
		}
		return this.primary.handleKeyTyped(typedChar, key);
	}
}