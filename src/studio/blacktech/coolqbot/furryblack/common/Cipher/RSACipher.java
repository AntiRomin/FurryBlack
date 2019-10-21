package studio.blacktech.coolqbot.furryblack.common.Cipher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSACipher {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private BASE64Encoder encoder;
	private BASE64Decoder decoder;

	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;

	private Cipher encrypter;
	private Cipher decrypter;

	public RSACipher() {
		this.encoder = new BASE64Encoder();
		this.decoder = new BASE64Decoder();
	}

	public RSACipher(String secretKey, int keyLength) {
		this();

		try {

			Provider provider = Security.getProvider("SUN");

			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			random.setSeed(secretKey.getBytes(UTF_8));

			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(keyLength, random);

			KeyPair keyPair = generator.generateKeyPair();

			this.publicKey = (RSAPublicKey) keyPair.getPublic();
			this.privateKey = (RSAPrivateKey) keyPair.getPrivate();

			this.encrypter = Cipher.getInstance("RSA");
			this.decrypter = Cipher.getInstance("RSA");

			this.encrypter.init(Cipher.ENCRYPT_MODE, this.publicKey);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.privateKey);

		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException exception) {
			exception.printStackTrace();
		}
	}

	public RSACipher(String publicKey, String privateKey) throws InvalidKeyException {
		this();

		try {

			byte[] publicKeyString = this.decoder.decodeBuffer(publicKey);
			byte[] privateKeyString = this.decoder.decodeBuffer(privateKey);

			KeyFactory factory = KeyFactory.getInstance("RSA");

			this.publicKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyString));
			this.privateKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyString));

			this.encrypter = Cipher.getInstance("RSA");
			this.decrypter = Cipher.getInstance("RSA");

			this.encrypter.init(Cipher.ENCRYPT_MODE, this.publicKey);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.privateKey);

		} catch (IOException | InvalidKeyException | InvalidKeySpecException exception) {

			exception.printStackTrace();
			throw new InvalidKeyException("传入了不合法的密钥");

		} catch (NoSuchAlgorithmException | NoSuchPaddingException exception) {
			exception.printStackTrace();

		}
	}

	public String encrypt(String content) throws GeneralSecurityException {
		byte[] tmp1 = content.getBytes(UTF_8);
		byte[] tmp2 = this.encrypter.doFinal(tmp1);
		return this.encoder.encode(tmp2);
	}

	public String decrypt(String content) throws GeneralSecurityException, IOException {
		byte[] tmp1 = this.decoder.decodeBuffer(content);
		byte[] tmp2 = this.decrypter.doFinal(tmp1);
		return new String(tmp2, UTF_8);
	}

	public String unsafeEncrypt(String content) {
		try {
			byte[] tmp1 = content.getBytes(UTF_8);
			byte[] tmp2 = this.encrypter.doFinal(tmp1);
			return this.encoder.encode(tmp2);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public String unsafeDecrypt(String content) {
		try {
			byte[] tmp1 = this.decoder.decodeBuffer(content);
			byte[] tmp2 = this.decrypter.doFinal(tmp1);
			return new String(tmp2, UTF_8);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public String getPublicKeyBase64() {
		return this.encoder.encode(this.publicKey.getEncoded());
	}

	public String getPrivateKeyBase64() {
		return this.encoder.encode(this.privateKey.getEncoded());
	}

}
