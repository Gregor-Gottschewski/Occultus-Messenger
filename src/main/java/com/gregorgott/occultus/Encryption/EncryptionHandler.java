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
import org.pgpainless.encryption_signing.EncryptionOptions;
import org.pgpainless.encryption_signing.EncryptionStream;
import org.pgpainless.encryption_signing.ProducerOptions;
import org.pgpainless.encryption_signing.SigningOptions;
import org.pgpainless.key.protection.SecretKeyRingProtector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Der {@code EncryptionHandler} hat die Aufgabe Daten zu entschlüsseln.
 * Dabei werden PGPainless Bibliotheken genutzt.
 * Die zu verschlüsselnde Nachricht wird bei der Initialisierung übergeben.
 * Der Klartext wird mit {@code getEncryptedMessage()} zurückgegeben.
 *
 * @author Gregor Gottschewski
 * @version 1.0.0
 * @since 2023-02-23 (YYYY-MM-DD)
 */
public class EncryptionHandler {
    private final byte[] text;
    private final byte[] userPrivateKey;
    private final byte[] recipientPublicKey;

    /**
     * Initialisiert den {@code EncryptionHandler}.
     *
     * @param text               der Text, der verschlüsselt werden soll.
     * @param userPrivateKey     der private Schlüssel der Senderinstanz.
     * @param recipientPublicKey der öffentliche Schlüssel der Empfängerinstanz
     * @since 1.0.0
     */
    public EncryptionHandler(String text, byte[] userPrivateKey, byte[] recipientPublicKey) {
        this.text = text.getBytes();
        this.userPrivateKey = userPrivateKey;
        this.recipientPublicKey = recipientPublicKey;
    }

    /**
     * Startet die Verschlüsselung und gibt das Chiffrat zurück.
     *
     * @return das verschlüsselte Chiffrat als String.
     * @throws IOException  wenn die Schlüssel nicht eingelesen werden können
     *                      oder ein anderes Problem bei der Verschlüsselung
     *                      auftritt.
     * @throws PGPException wenn ein Fehler bei der Signatur auftritt.
     * @since 1.0.0
     */
    public String getEncryptedMessage() throws IOException, PGPException {
        PGPPublicKeyRing recipientKey = PGPainless.readKeyRing()
                .publicKeyRing(recipientPublicKey);

        PGPSecretKeyRing signingKey = PGPainless.readKeyRing()
                .secretKeyRing(userPrivateKey);
        SecretKeyRingProtector protector = SecretKeyRingProtector
                .unprotectedKeys();

        EncryptionOptions encryptionOptions = EncryptionOptions.get()
                .addRecipient(recipientKey);
        SigningOptions signingOptions = SigningOptions.get()
                .addSignature(protector, signingKey);

        ProducerOptions options = ProducerOptions
                .signAndEncrypt(encryptionOptions, signingOptions);

        InputStream plaintext = new ByteArrayInputStream(text);
        ByteArrayOutputStream ciphertext = new ByteArrayOutputStream();

        EncryptionStream encryptionStream = PGPainless.encryptAndOrSign()
                .onOutputStream(ciphertext)
                .withOptions(options);

        Streams.pipeAll(plaintext, encryptionStream);
        encryptionStream.close();

        return ciphertext.toString();
    }
}
