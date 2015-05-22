package library;
import 	java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import android.util.Base64;

public class certify
{
	//constructor
	public static  PrivateKey privkeyencode;
	public certify()
	{
	
	}
	public KeyPair  genKeyPair()
	{
		try
		{
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			 privkeyencode = keyPair.getPrivate();
			return keyPair;
		}catch (NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
		
	}
	public String signData(String dollar,byte[] privkeyEncode)
	{
		try
		{
			Signature signature = Signature.getInstance("SHA1withRSA");
			/*KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privkeyEncode);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);*/
            signature.initSign(privkeyencode, SecureRandom.getInstance("SHA1PRNG"));
            signature.update(dollar.getBytes());
            byte[] sigBytes = signature.sign();
            String Sign = Base64.encodeToString(sigBytes, Base64.DEFAULT);
            return Sign;
		}catch(NoSuchAlgorithmException e){
		
		}catch(SignatureException e){
			
		}catch(InvalidKeyException e){
			
		}catch(Exception e){
			
		}
		return "NULL";
	}
	
}