package cn.wangtao.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @ClassName RSAUtils
 * @Auth 桃子
 * @Date 2019-6-25 15:15
 * @Version 1.0
 * @Description
 **/
public class RSAUtils {

    private static final String RSA = "RSA"; // 加密方式
    private static final Logger logger= LoggerFactory.getLogger(RSAUtils.class);

    //获取密钥
    public static KeyPair getKey() throws Exception {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA, new BouncyCastleProvider());
            keyPairGenerator.initialize(2048); // 初始化密钥长度
            KeyPair keyPair = keyPairGenerator.generateKeyPair();// 生成密钥对
            return keyPair;
        } catch (Exception e) {
            logger.error("获取RSA秘钥对异常",e);
            throw new Exception("获取RSA秘钥对异常",e);
        }
    }

    //利用公钥进行加密
    public static String encryptStr(RSAPublicKey publicKey, String str) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(RSA, new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //加密
            byte[] bytes = getBytes(str.getBytes(), cipher);
            //2进行转换成16进制
            String result = CommonUtils.parseByte2HexStr(bytes);
            return result;
        } catch (Exception e) {
            logger.error("使用RSA公钥进行加密异常",e);
            throw new Exception("使用RSA公钥进行加密异常",e);
        }
    }




    //利用私钥进行解密
    public static String decryptStr(RSAPrivateKey privateKey, String str) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(RSA, new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, privateKey); // 用密钥初始化此Cipher对象
            //16进制转换成2进制
            byte[] bytes = CommonUtils.parseHexStr2Byte(str);
            //解密
            byte[] bs = getBytes(bytes, cipher);
            String content=new String(bs,"utf-8");
            return content;
        } catch (Exception e) {
            logger.error("使用RSA私钥进行解密异常",e);
            throw new Exception("使用RSA私钥进行解密异常",e);
        }
    }


    //通过cipher获取字节数组
    public static byte[] getBytes(byte[] bytes,Cipher cipher) throws Exception {
        int blockSize = cipher.getBlockSize(); // 返回块的大小
        int j = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (bytes.length - j * blockSize > 0) { // 将二进制数据分块写入ByteArrayOutputStream中
            if(bytes.length-j*blockSize>blockSize){
                baos.write(cipher.doFinal(bytes, j * blockSize, blockSize));
            }else{
                baos.write(cipher.doFinal(bytes, j * blockSize,bytes.length-j*blockSize));
            }
            j++;
        }
        baos.close();
        byte[] byteArray = baos.toByteArray();
        return byteArray;
    }

    //保存秘钥对到文件
    public void saveRSAKey(String fileName) throws Exception {
        FileOutputStream fos=null;
        ObjectOutputStream oos=null;
        try {
            KeyPair keyPair = getKey();
            fos=new FileOutputStream(fileName);
            oos=new ObjectOutputStream(fos); //对象序列号
            oos.writeObject(keyPair);
        } catch (Exception e) {
            logger.error("RSA秘钥对保存到文件异常[{}]",fileName,e);
            throw new Exception("RSA秘钥对保存到文件异常",e);
        }finally {
            if(oos!=null){
                try {
                    oos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
