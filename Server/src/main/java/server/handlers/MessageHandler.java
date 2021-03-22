package server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import messages.*;
import server.Config;
import server.auth.UserConfig;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    private final UserConfig USER_CONFIG;
    private final Path FULL_PATH;
    private BufferedOutputStream bos;
    private String curUUID;

    public MessageHandler(UserConfig userConfig) {
        this.USER_CONFIG = userConfig;
        this.FULL_PATH = Config.getRootFolder().resolve(userConfig.getHomePath());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (((Message) msg).getType() == MessageType.DELETE_FILE) {
            DeleteMessage message = (DeleteMessage) msg;
            Files.deleteIfExists(FULL_PATH.resolve(Paths.get(message.getFileName())));
        } else if (((Message) msg).getType() == MessageType.DATA) {

            DataMessage message = (DataMessage) msg;

            if (!message.getUuid().equals(curUUID)) {
                curUUID = message.getUuid();
                bos = new BufferedOutputStream(
                        new FileOutputStream(FULL_PATH.resolve(Paths.get(message.getFileName())).toString()));
            }

            bos.write(message.getData());

            if (message.isFinalPart()) {
                bos.close();
                bos = null;
            }
        } else if (((Message) msg).getType() == MessageType.RENAME_FILE) {
            RenameMessage message = (RenameMessage) msg;
            Files.move(FULL_PATH.resolve(Paths.get(message.getOldName())),
                    FULL_PATH.resolve(Paths.get(message.getFileName())));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
