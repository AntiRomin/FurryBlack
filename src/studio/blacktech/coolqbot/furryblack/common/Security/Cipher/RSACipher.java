package studio.blacktech.coolqbot.furryblack.common.Security.Cipher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 使用标准JavaCipher包装的RSA工具类，包含三种加密模式：标准加密、使用HA-384进行消息验证、使用签名后不初始化的SHA-384进行消息验证。
 *
 * 带消息验证的数据帧为：
 *
 * 00 00 00 00 , 00 00 00 00 - 00 00 00 00 , 00 00 00 00 - XXXX
 *
 * 前8位 原始消息getBytes(UTF-8)后数组的长度 int → hexString → getBytes(UTF-8)
 *
 * 后8位 SHA-384的前8位
 *
 * 之后为原始数据getBytes(UTF-8)
 *
 * 数据帧经过RSA加密和Base64编码，成为密文。
 *
 * @author Alceatraz Warprays
 *
 */
public class RSACipher {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private Cipher encrypter;
	private Cipher decrypter;
	private BASE64Encoder encoder;
	private BASE64Decoder decoder;
	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;
	private MessageDigest staticDigester;
	private MessageDigest oneoffDigester;

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	/**
	 * 构造方法
	 *
	 * @param secretKey 随机种子，作为密钥生成器的随机数生成器的种子
	 * @param keyLength 密钥长度，至少为512
	 * @throws InvalidKeyException 错误的密钥
	 */
	public RSACipher(String secretKey, int keyLength) throws InvalidKeyException {
		this(generateKeyPair(secretKey, keyLength));
	}

	/**
	 * 构造方法
	 *
	 * @param keyPair 密钥对
	 */
	public RSACipher(KeyPair keyPair) {
		this((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
	}

	/**
	 * 构造方法
	 *
	 * @param publicKey  Base64编码的X509格式公钥
	 * @param privateKey Base64编码的PKCS8格式私钥
	 * @throws InvalidPublicKeyException  公钥格式错误
	 * @throws InvalidPrivateKeyException 私钥格式错误
	 */
	public RSACipher(String publicKey, String privateKey) throws InvalidPublicKeyException, InvalidPrivateKeyException {
		this(getRSAPublicKeyFromString(publicKey), getRSAPrivateKeyFromString(privateKey));
	}

	/**
	 * 构造方法
	 *
	 * @param publicKey  RSA公钥
	 * @param privateKey RSA私钥
	 */
	public RSACipher(RSAPublicKey publicKey, RSAPrivateKey privateKey) {

		try {

			this.publicKey = publicKey;
			this.privateKey = privateKey;

			this.encrypter = Cipher.getInstance("RSA");
			this.decrypter = Cipher.getInstance("RSA");

			this.encrypter.init(Cipher.ENCRYPT_MODE, this.publicKey);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.privateKey);

			this.encoder = new BASE64Encoder();
			this.decoder = new BASE64Decoder();

			this.staticDigester = MessageDigest.getInstance("SHA-384");
			this.oneoffDigester = MessageDigest.getInstance("SHA-384");

		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// InvalidKeyException ---------------- 由密钥生成器生成，输入密钥错误已经在上一级构造方法抛出
			// NoSuchPaddingException ------------- 不允许用户自定义算法
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}

	private static RSAPublicKey getRSAPublicKeyFromString(String publicKey) throws InvalidPublicKeyException {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			byte[] publicKeyString = new BASE64Decoder().decodeBuffer(publicKey);
			return (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyString));
		} catch (IOException | InvalidKeySpecException exception) {
			throw new InvalidPublicKeyException("Invalidate publickey, make sure is formated as X509 and encode with BASE64.");
		} catch (NoSuchAlgorithmException exception) {
			return null;
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}

	private static RSAPrivateKey getRSAPrivateKeyFromString(String privateKey) throws InvalidPrivateKeyException {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			byte[] privateKeyString = new BASE64Decoder().decodeBuffer(privateKey);
			return (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyString));
		} catch (IOException | InvalidKeySpecException exception) {
			throw new InvalidPrivateKeyException("Invalidate publickey, make sure is formated as X509 and encode with BASE64.");
		} catch (NoSuchAlgorithmException exception) {
			return null;
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}

	}

	private static KeyPair generateKeyPair(String randomSeed, int keyLength) {
		try {

			Provider provider = Security.getProvider("SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			random.setSeed(randomSeed.getBytes(UTF_8));

			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(keyLength, random);

			return generator.generateKeyPair();

		} catch (NoSuchAlgorithmException exception) {
			return null;
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	/**
	 * 加密模式1
	 *
	 * @param content 原文
	 * @return 密文
	 */
	public String encrypt(String content) {

		try {

			byte[] tmp1 = content.getBytes(UTF_8);
			byte[] tmp2 = this.encrypter.doFinal(tmp1);
			return this.encoder.encode(tmp2);

		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			exception.printStackTrace();
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	/**
	 * 解密模式1
	 *
	 * @param content 密文
	 * @return 原文
	 * @throws IOException 输入错误的内容
	 */
	public String decrypt(String content) throws IOException {

		try {

			byte[] tmp1 = this.decoder.decodeBuffer(content);
			byte[] tmp2 = this.decrypter.doFinal(tmp1);
			return new String(tmp2, UTF_8);

		} catch (IOException exception) {
			exception.printStackTrace();
			throw exception;

		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			exception.printStackTrace();
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	/**
	 * 加密模式2
	 *
	 * @param content 原文
	 * @return 密文
	 */
	public String encryptHash(String content) {

		try {

			byte[] rawMessage = content.getBytes(UTF_8);

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int rawMessageLength = rawMessage.length;
			byte[] result = new byte[16 + rawMessageLength];

			sizePart = Integer.toHexString(rawMessageLength).getBytes(UTF_8);
			int sizePartLength = sizePart.length;
			System.arraycopy(sizePart, 0, result, 8 - sizePartLength, sizePartLength);

			this.oneoffDigester.update(rawMessage);
			hashPart = this.oneoffDigester.digest();
			System.arraycopy(hashPart, 0, result, 8, 8);

			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);

			return this.encoder.encode(this.encrypter.doFinal(result));

		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	/**
	 * 解密模式2
	 *
	 * @param content 密文
	 * @return 原文
	 * @throws IOException                     输入了错误的内容
	 * @throws MessageSizeCheckFailedException 消息长度验证不通过
	 * @throws MessageHashCheckFailedException 消息哈希验证不通过
	 */
	public String decryptHash(String content) throws IOException, MessageSizeCheckFailedException, MessageHashCheckFailedException {

		try {

			byte[] rawMessage = this.decrypter.doFinal(this.decoder.decodeBuffer(content));

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int actualMessageLength = rawMessage.length - 16;

			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claminMessagelength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claminMessagelength != actualMessageLength) { throw new MessageSizeCheckFailedException(claminMessagelength, actualMessageLength); }

			System.arraycopy(rawMessage, 8, hashPart, 0, 8);

			byte[] mesgPart = new byte[claminMessagelength];
			System.arraycopy(rawMessage, 16, mesgPart, 0, claminMessagelength);
			this.oneoffDigester.update(mesgPart);
			byte[] digest = this.oneoffDigester.digest();

			if (!isSame(hashPart, digest)) { throw new MessageHashCheckFailedException(hashPart, digest); }

			return new String(mesgPart, UTF_8);

		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	public String encraptPhaseHash(String content) {

		try {

			byte[] rawMessage = content.getBytes(UTF_8);

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int rawMessageLength = rawMessage.length;
			byte[] result = new byte[16 + rawMessageLength];

			sizePart = Integer.toHexString(rawMessageLength).getBytes(UTF_8);
			int sizePartLength = sizePart.length;
			System.arraycopy(sizePart, 0, result, 8 - sizePartLength, sizePartLength);

			this.staticDigester.update(rawMessage);
			hashPart = ((MessageDigest) this.staticDigester.clone()).digest();
			System.arraycopy(hashPart, 0, result, 8, 8);

			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);

			return this.encoder.encode(this.encrypter.doFinal(result));

		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException -------- 不允许用户自定义算法
			// IllegalBlockSizeException -- 不允许用户传入byte[]
			// CloneNotSupportedException - MessageDigest是能够克隆的
		}
	}

	public String decryptPhaseHash(String content) throws IOException, MessageSizeCheckFailedException, MessageHashCheckFailedException {
		try {

			byte[] rawMessage = this.decrypter.doFinal(this.decoder.decodeBuffer(content));

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int actualMessageLength = rawMessage.length - 16;

			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claminMessagelength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claminMessagelength != actualMessageLength) { throw new MessageSizeCheckFailedException(claminMessagelength, actualMessageLength); }

			System.arraycopy(rawMessage, 8, hashPart, 0, 8);

			byte[] mesgPart = new byte[claminMessagelength];
			System.arraycopy(rawMessage, 16, mesgPart, 0, claminMessagelength);

			this.staticDigester.update(mesgPart);
			byte[] digest = ((MessageDigest) this.staticDigester.clone()).digest();

			if (!isSame(hashPart, digest)) { throw new MessageHashCheckFailedException(hashPart, digest); }

			return new String(mesgPart, UTF_8);

		} catch (IOException exception) {
			exception.printStackTrace();
			throw exception;

		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// NoSuchAlgorithmException -- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	private static boolean isSame(byte[] A, byte[] B) {
		// 只发送前8位，所以只比较前8位，java没有数组截取 "python[0:7]" 所以只能写的这么蠢
		// @formatter:off
		return	(A[0] == B[0]) && (A[1] == B[1]) && (A[2] == B[2]) && (A[3] == B[3]) &&
				(A[4] == B[4]) && (A[5] == B[5]) && (A[6] == B[6]) && (A[7] == B[7]) ;
		// @formatter:on
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public String getEncodedPublicKey() {
		return this.encoder.encode(this.publicKey.getEncoded());
	}

	public String getEncodedPrivateKey() {
		return this.encoder.encode(this.privateKey.getEncoded());
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public static class MessageSizeCheckFailedException extends GeneralSecurityException {
		private static final long serialVersionUID = 0;

		public MessageSizeCheckFailedException(int claimSize, int actualSize) {
			super("Message claim length is " + claimSize + ", But actual length is " + actualSize);
		}
	}

	public static class MessageHashCheckFailedException extends GeneralSecurityException {
		private static final long serialVersionUID = 0;

		public MessageHashCheckFailedException(byte[] claimHash, byte[] actualHash) {
			super("Message claim digest is " + Arrays.toString(claimHash) + ", But actual digest is " + Arrays.toString(Arrays.copyOfRange(actualHash, 0, 8)));
		}
	}

	public static class InvalidPublicKeyException extends InvalidKeyException {
		private static final long serialVersionUID = 0;

		public InvalidPublicKeyException(String message) {
			super(message);
		}
	}

	public static class InvalidPrivateKeyException extends InvalidKeyException {
		private static final long serialVersionUID = 0;

		public InvalidPrivateKeyException(String message) {
			super(message);
		}
	}
}
