package library;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
 
import android.util.Base64;
 

public class RSA {
    /*private static final String RSA_PUBLICE =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA33/aY6LtC1JEh7EvFk89" + "\r" +
            "mNzsP+QjKqq17bji/m29KqBmgQfaBz9kpPso0ICgcqdMPx6R82dcjEmojoINSfMU" + "\r" +
            "5jTcozRmgUEF7D0LTCQTyiO+iOOwCUIwHrKsOgizgpiwphqsWlskX8IN8gPgDhoJ" + "\r" +
            "rixC6Ixq+llKjk3sgCe4DtTtVJ9oDHICZi1tb4EHg6tD2Athn8/FnEKBl91h4nh+" + "\r" +
            "FlHUv0ZoKRQvXVN6TPbfbeiG2x2bCyywAuT0KPj3qRIQObHr3ySZ6l7Sae/Uw2BZ" + "\r" +
            "VvcHVUC0hhvJch8WYPPE/8t9I6GQHMBBYijdTrQzGLZWcNCiFbJ6CyLZF/tE2gzJ" + "\r" +
            "xwIDAQAB";*/
	public static String RSA_PUBLICE;
    private static final String ALGORITHM = "RSA";
 
    //get public key from X509
    private static PublicKey getPublicKeyFromX509(String algorithm,String bysKey) throws NoSuchAlgorithmException, Exception
    {
        byte[] decodedKey = Base64.decode(bysKey,Base64.DEFAULT);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);
 
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(x509);
    }
 
    //encrypt the message
    public static String encryptByPublic(String content) {
        try {
            PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);
 
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubkey);
 
            byte plaintext[] = content.getBytes("UTF-8");
            byte[] output = cipher.doFinal(plaintext);
 
            String s = new String(Base64.encode(output,Base64.DEFAULT));
 
            return s;
 
        } catch (Exception e) {
            return null;
        }
    }
 
    
    public static String decryptByPublic(String content) {
        try {
            PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pubkey);
            InputStream ins = new ByteArrayInputStream(Base64.decode(content,Base64.DEFAULT));
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            byte[] buf = new byte[128];
            int bufl;
            while ((bufl = ins.read(buf)) != -1) {
                byte[] block = null;
                if (buf.length == bufl) {
                block = buf;
                } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
                }
                writer.write(cipher.doFinal(block));
            }
            return new String(writer.toByteArray(), "utf-8");
        } catch (Exception e) {
            return null;
        }
    }
 
}