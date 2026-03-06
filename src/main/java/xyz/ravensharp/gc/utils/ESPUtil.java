package xyz.ravensharp.gc.utils;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public class ESPUtil {

	public static void renderHPBar(Minecraft mc, Entity entity, float partialTicks, boolean left,
			boolean renderThroughWall) {
		if (!(entity instanceof net.minecraft.entity.EntityLivingBase))
			return;

		net.minecraft.entity.EntityLivingBase living = (net.minecraft.entity.EntityLivingBase) entity;

		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks
				- mc.getRenderManager().viewerPosX;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks
				- mc.getRenderManager().viewerPosY;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks
				- mc.getRenderManager().viewerPosZ;

		float maxHp = living.getMaxHealth();
		float hp = Math.max(0f, living.getHealth());
		float hpPercent = hp / maxHp;

		float height = entity.height;
		float filledHeight = height * hpPercent;

		float espWidth = entity.width / 1.5f;
		float barWidth = 0.035f;
		float offset = 0.1f;
		float xCenter = (left ? 1f : -1f) * (espWidth + offset + barWidth);
		float x1 = xCenter - barWidth;
		float x2 = xCenter + barWidth;

		float r, g, b;
		if (hpPercent > 0.66f) {
			r = 0.1f;
			g = 1f;
			b = 0.1f;
		} else if (hpPercent > 0.33f) {
			r = 1f;
			g = 1f;
			b = 0.1f;
		} else {
			r = 1f;
			g = 0.1f;
			b = 0.1f;
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (renderThroughWall) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		} else {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		GL11.glDepthMask(false);

		GL11.glColor4f(0.3f, 0.3f, 0.3f, 1f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(x1, 0.0, 0.0);
		GL11.glVertex3d(x2, 0.0, 0.0);
		GL11.glVertex3d(x2, height, 0.0);
		GL11.glVertex3d(x1, height, 0.0);
		GL11.glEnd();

		GL11.glColor4f(r, g, b, 1f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(x1, 0.0, 0.0);
		GL11.glVertex3d(x2, 0.0, 0.0);
		GL11.glVertex3d(x2, filledHeight, 0.0);
		GL11.glVertex3d(x1, filledHeight, 0.0);
		GL11.glEnd();

		float distance = (float) Math.sqrt(x * x + y * y + z * z);
		float weight = Math.max(0.5f, 1.5f / (distance * 0.5f));
		GL11.glLineWidth(weight);

		GL11.glColor4f(0f, 0f, 0f, 1f);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, 0.0, 0.0);
		GL11.glVertex3d(x1, height, 0.0);
		GL11.glVertex3d(x2, 0.0, 0.0);
		GL11.glVertex3d(x2, height, 0.0);
		GL11.glVertex3d(x1, height, 0.0);
		GL11.glVertex3d(x2, height, 0.0);
		GL11.glVertex3d(x1, 0.0, 0.0);
		GL11.glVertex3d(x2, 0.0, 0.0);
		GL11.glEnd();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	public static void renderESP(Minecraft mc, Entity entity, float partialTicks, Color color, boolean drawBackground,
			boolean is2D, float lineOpacity, float bgOpacity) {
		RenderManager rm = mc.getRenderManager();

		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - rm.viewerPosX;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - rm.viewerPosY;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - rm.viewerPosZ;

		float width = entity.width / 1.5f;
		float height = entity.height;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		if (is2D) {
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);

		if (drawBackground) {
			GL11.glColor4f(0.0f, 0.0f, 0.0f, bgOpacity);
			drawBox(width, height, GL11.GL_QUADS, is2D);
		}

		GL11.glLineWidth(3.5f);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, lineOpacity);
		drawBox(width, height, GL11.GL_LINES, is2D);

		GL11.glLineWidth(1.5f);
		GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, lineOpacity);
		drawBox(width, height, GL11.GL_LINES, is2D);

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawBox(float w, float h, int mode, boolean is2D) {
		if (mode == GL11.GL_QUADS) {
			GL11.glBegin(GL11.GL_QUADS);
			if (is2D) {
				GL11.glVertex3d(-w, 0, 0);
				GL11.glVertex3d(w, 0, 0);
				GL11.glVertex3d(w, h, 0);
				GL11.glVertex3d(-w, h, 0);
			} else {
				GL11.glVertex3d(-w, 0, -w);
				GL11.glVertex3d(w, 0, -w);
				GL11.glVertex3d(w, 0, w);
				GL11.glVertex3d(-w, 0, w);

				GL11.glVertex3d(-w, h, -w);
				GL11.glVertex3d(-w, h, w);
				GL11.glVertex3d(w, h, w);
				GL11.glVertex3d(w, h, -w);

				GL11.glVertex3d(-w, 0, -w);
				GL11.glVertex3d(-w, h, -w);
				GL11.glVertex3d(w, h, -w);
				GL11.glVertex3d(w, 0, -w);

				GL11.glVertex3d(-w, 0, w);
				GL11.glVertex3d(w, 0, w);
				GL11.glVertex3d(w, h, w);
				GL11.glVertex3d(-w, h, w);

				GL11.glVertex3d(-w, 0, -w);
				GL11.glVertex3d(-w, 0, w);
				GL11.glVertex3d(-w, h, w);
				GL11.glVertex3d(-w, h, -w);

				GL11.glVertex3d(w, 0, -w);
				GL11.glVertex3d(w, h, -w);
				GL11.glVertex3d(w, h, w);
				GL11.glVertex3d(w, 0, w);
			}
			GL11.glEnd();
		} else if (mode == GL11.GL_LINES) {
			GL11.glBegin(GL11.GL_LINES);
			if (is2D) {
				GL11.glVertex3d(-w, 0, 0);
				GL11.glVertex3d(w, 0, 0);
				GL11.glVertex3d(w, 0, 0);
				GL11.glVertex3d(w, h, 0);
				GL11.glVertex3d(w, h, 0);
				GL11.glVertex3d(-w, h, 0);
				GL11.glVertex3d(-w, h, 0);
				GL11.glVertex3d(-w, 0, 0);
			} else {
				GL11.glVertex3d(-w, 0, -w);
				GL11.glVertex3d(w, 0, -w);
				GL11.glVertex3d(w, 0, -w);
				GL11.glVertex3d(w, 0, w);
				GL11.glVertex3d(w, 0, w);
				GL11.glVertex3d(-w, 0, w);
				GL11.glVertex3d(-w, 0, w);
				GL11.glVertex3d(-w, 0, -w);
				GL11.glVertex3d(-w, h, -w);
				GL11.glVertex3d(w, h, -w);
				GL11.glVertex3d(w, h, -w);
				GL11.glVertex3d(w, h, w);
				GL11.glVertex3d(w, h, w);
				GL11.glVertex3d(-w, h, w);
				GL11.glVertex3d(-w, h, w);
				GL11.glVertex3d(-w, h, -w);
				GL11.glVertex3d(-w, 0, -w);
				GL11.glVertex3d(-w, h, -w);
				GL11.glVertex3d(w, 0, -w);
				GL11.glVertex3d(w, h, -w);
				GL11.glVertex3d(w, 0, w);
				GL11.glVertex3d(w, h, w);
				GL11.glVertex3d(-w, 0, w);
				GL11.glVertex3d(-w, h, w);
			}
			GL11.glEnd();
		}
	}
}