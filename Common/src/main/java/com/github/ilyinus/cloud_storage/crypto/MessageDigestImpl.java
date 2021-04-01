package com.github.ilyinus.cloud_storage.crypto;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestImpl implements CryptoService {
    MessageDigest md;

    public MessageDigestImpl(String algorithm, byte[] bytes) {

        try {
            this.md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (bytes != null) {
            update(bytes, bytes.length);
        }

    }

    public MessageDigestImpl(String algorithm) {
        this(algorithm, null);
    }

    @Override
    public void update(byte[] bytes, int len) {
        md.update(bytes, 0, len);
    }

    @Override
    public String getHash() {
        byte[] digest = md.digest();
        return new HexBinaryAdapter().marshal(digest);
    }
}
