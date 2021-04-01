package com.github.ilyinus.cloud_storage.crypto;

public interface CryptoService {
    void update(byte[] bytes, int len);
    String getHash();
}
