package xyz.ravensharp.gc.module.modules.combat;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;

public class JumpReset extends Module {
	private final ChannelInboundHandlerAdapter packetListener;

	public JumpReset() {
		super("JumpReset", "Reduce Knockbacks.", Category.COMBAT);
		this.packetListener = new VelocityListener();
	}

	@Sharable
	private class VelocityListener extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) msg;
				if (mc.thePlayer != null && packet.getEntityID() == mc.thePlayer.getEntityId()) {
					mc.addScheduledTask(() -> cancellation());
				}
			}
			super.channelRead(ctx, msg);
		}
	}

	@Override
	public void onEnable() {
		super.onEnable();
		inject();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		remove();
	}

	@SubscribeEvent
	public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if (this.isToggled()) {
			ChannelPipeline pipeline = event.manager.channel().pipeline();
			if (pipeline.get("velocity_listener") == null) {
				pipeline.addBefore("packet_handler", "velocity_listener", packetListener);
			}
		}
	}

	private void inject() {
		if (mc.thePlayer != null && mc.getNetHandler() != null) {
			try {
				ChannelPipeline pipeline = mc.getNetHandler().getNetworkManager().channel().pipeline();
				if (pipeline.get("velocity_listener") == null) {
					pipeline.addBefore("packet_handler", "velocity_listener", packetListener);
				}
			} catch (Exception e) {
			}
		}
	}

	private void remove() {
		if (mc.thePlayer != null && mc.getNetHandler() != null) {
			try {
				ChannelPipeline pipeline = mc.getNetHandler().getNetworkManager().channel().pipeline();
				if (pipeline.get("velocity_listener") != null) {
					pipeline.remove("velocity_listener");
				}
			} catch (Exception e) {
			}
		}
	}

	public void cancellation() {
		if (!mc.inGameHasFocus || mc.currentScreen != null)
			return;
		if (!Sharp.moduleManager.getModule("JumpReset").isToggled())
			return;

		if (mc.thePlayer.onGround) {
			mc.thePlayer.jump();
		}
	}
}