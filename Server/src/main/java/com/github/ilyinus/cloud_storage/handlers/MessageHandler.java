package com.github.ilyinus.cloud_storage.handlers;

import com.github.ilyinus.cloud_storage.crypto.CryptoService;
import com.github.ilyinus.cloud_storage.crypto.MessageDigestImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.github.ilyinus.cloud_storage.messages.*;
import com.github.ilyinus.cloud_storage.config.Config;
import com.github.ilyinus.cloud_storage.auth.UserConfig;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    private final UserConfig USER_CONFIG;
    private final Path FULL_PATH;
    private BufferedOutputStream bos;
    private CryptoService crypto;
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
                crypto = new MessageDigestImpl("md5");
                curUUID = message.getUuid();
                bos = new BufferedOutputStream(
                        new FileOutputStream(FULL_PATH.resolve(Paths.get(message.getFileName())).toString()));
            }

            byte[] dataPart = message.getData();
            bos.write(dataPart);
            crypto.update(dataPart, dataPart.length);

            if (message.isFinalPart()) {
                bos.close();
                bos = null;

                if (message.getMd5().equals(crypto.getHash())) {
                    ctx.writeAndFlush(new ApproveMessage());
                } else {
                    Files.deleteIfExists(FULL_PATH.resolve(Paths.get(message.getFileName())));
                    Message errorMessage = new ErrorMessage("File has been corrupted");
                    ctx.writeAndFlush(errorMessage);
                }

            }
        } else if (((Message) msg).getType() == MessageType.RENAME_FILE) {
            RenameMessage message = (RenameMessage) msg;
            Files.move(FULL_PATH.resolve(Paths.get(message.getOldName())),
                    FULL_PATH.resolve(Paths.get(message.getFileName())));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
