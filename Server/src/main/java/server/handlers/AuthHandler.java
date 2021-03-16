package server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import messages.AuthMessage;
import messages.Message;
import messages.MessageType;
import server.auth.AuthService;
import server.auth.UserConfig;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean isAuthorized;
    private AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!isAuthorized && ((Message) msg).getType() == MessageType.AUTH) {
            AuthMessage message = (AuthMessage) msg;
            UserConfig userConfig = authService.authorize(message.getUsername(), message.getPassword());
            if (userConfig.isAuthorized()) {
                isAuthorized = true;
                ctx.pipeline().addLast(new MessageHandler(userConfig));
                ctx.writeAndFlush(new AuthMessage(MessageType.AUTH_OK));
            } else {
                AuthMessage answer = new AuthMessage(MessageType.AUTH_FAIL);
                answer.setDescription("Incorrect username or password");
                ctx.writeAndFlush(answer);
            }
        } else if (isAuthorized) {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
