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
import org.pgpainless.PGPainless;
import org.pgpainless.key.generation.type.rsa.RsaLength;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

/**
 * Die {@code KeyManager} Klasse kann Schlüsselpaare generieren.
 * Dafür wird die {@code PGPainless} Klasse aus der
 * PGPainless Bibliothek genutzt.
 * Da das Schlüsselpaar abhängig von der Inhaberinstanz
 * ist, wird bei der Initialisierung der Nutzername der
 * Inhaberinstanz benötigt.
 *
 * @author Gregor Gottschewski
 * @version 1.0.0
 * @since 2022-02-23 (YYYY-MM-DD)
 */
public class KeyManager {
    private final String userId;
    private PGPSecretKeyRing secretKey;
    private PGPPublicKeyRing publicKey;

    /**
     * Initialisieren der Klasse.
     *
     * @param userId der Nutzername, für den das Schlüsselpaar
     *               generiert werden soll.
     * @since 1.0.0
     */
    public KeyManager(String userId) {
        this.userId = userId;
    }

    /**
     * Generiert das Schlüsselpaar.
     *
     * @throws PGPException wenn ein Fehler bei der Schlüsselgenerierung
     *                      auftritt.
     * @throws InvalidAlgorithmParameterException wenn der ein Algorithmus
     *                                            Fehler auftritt.
     * @throws NoSuchAlgorithmException wenn der angegebene Algorithmus
     *                                  nicht existiert.
     * @since 1.0.0
     */
    public void generateKeyPair() throws PGPException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        secretKey = PGPainless.generateKeyRing().simpleRsaKeyRing(userId, RsaLength._4096);
        publicKey = PGPainless.extractCertificate(secretKey);
    }

    /**
     * Gibt den privaten Schlüssel als Byte-Array zurück.
     *
     * @return privater Schlüssel als Byte-Array.
     * @throws IOException wenn ein Radix Fehler auftritt.
     * @since 1.0.0
     */
    public byte[] getPrivateKey() throws IOException {
        return PGPainless.asciiArmor(secretKey).getBytes();
    }

    /**
     * Gibt den öffentlichen Schlüssel als Byte-Array zurück.
     *
     * @return öffentlicher Schlüssel als Byte-Array.
     * @throws IOException wenn ein Radix Fehler auftritt.
     * @since 1.0.0.
     */
    public byte[] getPublicKey() throws IOException {
        return PGPainless.asciiArmor(publicKey).getBytes();
    }
}
