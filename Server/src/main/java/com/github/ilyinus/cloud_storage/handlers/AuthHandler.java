package com.github.ilyinus.cloud_storage.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.github.ilyinus.cloud_storage.messages.AuthMessage;
import com.github.ilyinus.cloud_storage.messages.Message;
import com.github.ilyinus.cloud_storage.messages.MessageType;
import com.github.ilyinus.cloud_storage.auth.AuthService;
import com.github.ilyinus.cloud_storage.auth.UserConfig;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (((Message) msg).getType() == MessageType.AUTH) {
            AuthMessage message = (AuthMessage) msg;
            UserConfig userConfig = authService.authorize(message.getUsername(), message.getPassword());
            if (userConfig.isAuthorized()) {
                ctx.pipeline().addLast(new MessageHandler(userConfig));
                ctx.writeAndFlush(new AuthMessage(MessageType.AUTH_OK));
                ctx.pipeline().remove(this);
            } else {
                AuthMessage answer = new AuthMessage(MessageType.AUTH_FAIL);
                answer.setDescription("Incorrect username or password");
                ctx.writeAndFlush(answer);
            }
        } else {
            AuthMessage answer = new AuthMessage(MessageType.AUTH_FAIL);
            answer.setDescription("Incorrect message type");
            ctx.writeAndFlush(answer);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
