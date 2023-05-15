/*
 * Copyright (c) 2023 Gregor Gottschewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gregorgott.occultus.Encryption;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.util.io.Streams;
import org.pgpainless.PGPainless;
import org.pgpainless.decryption_verification.ConsumerOptions;
import org.pgpainless.decryption_verification.DecryptionStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class decrypts messages with PGPainless and Bouncy Castle.
 * To initialize this class the user's private key is needed.
 * Afterwards, you can decrypt messages with this key.
 * If you want to change the key, you have to initialize
 * a new instance.
 *
 * @author Gregor Gottschewski
 * @version 1.0.0
 * @since 2022-02-23 (YYYY-MM-DD)
 */
public class DecryptionHandler {
    private final byte[] userPrivateKey;

    /**
     * Initialize.
     *
     * @param userPrivateKey the user's private key.
     * @since 1.0.0
     */
    public DecryptionHandler(byte[] userPrivateKey) {
        this.userPrivateKey = userPrivateKey;
    }

    /**
     * Decrypts the given text and returns it as a decrypted string.
     *
     * @return the decrypted text.
     * @param text the text to decrypt.
     * @param senderPublicKey the public key of the message's sender.
     * @throws IOException  if the key could not been read or another
     *                      decryption problem occurs.
     * @throws PGPException if an verification error occurs.
     * @since 1.0.0
     */
    public String getDecryptedMessage(byte[] senderPublicKey, String text) throws IOException, PGPException {
        byte[] textBytes = text.getBytes();

        PGPPublicKeyRing verificationCert = PGPainless.readKeyRing()
                .publicKeyRing(senderPublicKey);
        PGPSecretKeyRing secretKey = PGPainless.readKeyRing()
                .secretKeyRing(userPrivateKey);

        ConsumerOptions options = ConsumerOptions.get()
                .addVerificationCert(verificationCert)
                .addDecryptionKey(secretKey);

        InputStream ciphertext = new ByteArrayInputStream(textBytes);
        ByteArrayOutputStream plaintext = new ByteArrayOutputStream();

        DecryptionStream consumerStream = PGPainless.decryptAndOrVerify()
                .onInputStream(ciphertext)
                .withOptions(options);

        Streams.pipeAll(consumerStream, plaintext);
        consumerStream.close();

        return plaintext.toString();
    }
}
