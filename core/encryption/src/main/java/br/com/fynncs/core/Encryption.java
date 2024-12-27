package br.com.fynncs.core;

import br.com.fynncs.core.interfaces.IEncryption;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encryption implements IEncryption {

    public final String KEY = "`H}d@+MsdL*glS.`h`H\"&+RC8V4c&BaiQYG4jfx-gGk;DiA^|RU\\U|<2eX<OB[Zi";

    @Override
    public String encrypt(String text, String key) throws InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec ps = new PBEParameterSpec(new byte[]{3, 1, 4, 1, 5, 9, 2, 6}, 20);
        KeySpec ks = new PBEKeySpec(key.toCharArray());
        SecretKey skey = skf.generateSecret(ks);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(Cipher.ENCRYPT_MODE, skey, ps);
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
    }

    @Override
    public String decrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        KeySpec ks = new PBEKeySpec(key.toCharArray());
        PBEParameterSpec ps = new PBEParameterSpec(new byte[]{3, 1, 4, 1, 5, 9, 2, 6}, 20);
        SecretKey skey = skf.generateSecret(ks);
        cipher.init(Cipher.DECRYPT_MODE, skey, ps);
        try {
            return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            throw new Exception("Error decrypt text!", ex);
        }
    }
}
