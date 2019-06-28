package cn.wangtao.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;

/**
 * @ClassName AESUtils
 * @Auth 桃子
 * @Date 2019-6-27 12:05
 * @Version 1.0
 * @Description
 **/
public class AESUtils {

    private static final Logger logger= LoggerFactory.getLogger(AESUtils.class);

    //填充类型
    public static final String AES_TYPE = "AES/ECB/PKCS5Padding";

    private static final String AES = "AES"; // 加密方式


    public static final String DES_TYPE = "DES/ECB/PKCS5Padding";

    private static final String DES = "DES"; // 加密方式

    private final  String defaultDesKey="11112222";//8位

    //对字符串加密
    public static String encryptStr(String content,String aesKey) throws Exception {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(),AES );
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //字符补全
            String content16Str = CommonUtils.completionCodeFor16Bytes(content);
            byte[] encryptedData = cipher.doFinal(content16Str.getBytes(CommonUtils.CODE_TYPE));
            //2进制转换成16进制
            String hexStr = CommonUtils.parseByte2HexStr(encryptedData);
            return hexStr;
        } catch (Exception e) {
            logger.error("使用AES对字符串加密异常",e);
            throw new Exception("使用AES对字符串加密异常",e);
        }

    }
    //对字符串解密
    public static String  decryptStr(String content,String aesKey) throws Exception {
        try {
            //16进制转换成2进制
            byte[] bytes = CommonUtils.parseHexStr2Byte(content);
            SecretKeySpec key = new SecretKeySpec(
                    aesKey.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedData = cipher.doFinal(bytes);
            String result=new String(decryptedData, CommonUtils.CODE_TYPE);
            //还原字符
            String orgResult = CommonUtils.resumeCodeOf16Bytes(result);
            return orgResult;
        } catch (Exception e) {
            logger.error("使用AES对字符串解密异常",e);
            throw new Exception("使用AES对字符串解密异常",e);
        }
    }

    //对文件加密
    public static File encryptFile(File orgFile, File encryptFile, Map<String,Object> context) throws Exception {
        logger.info("使用AES对文件加密开始，源文件地址[{}]加密后文件地址[{}]",orgFile.getPath(),encryptFile.getPath());
        BufferedReader br=null;
        BufferedWriter bw=null;
        try{
            //获取AESKEY ，如果没有为默认
            String aesKey = (String) context.get(Dirt.AES_KEY);
            br=new BufferedReader(new FileReader(orgFile));

            bw=(BufferedWriter)context.get(Dirt.BUFFEREDWRITER);
            if(null==bw){
                bw=new BufferedWriter(new FileWriter(encryptFile));
            }
            String len=null;
            while (null!=(len=br.readLine())){
                String encrypt= encryptStr(len,aesKey);
                bw.write(encrypt);
                bw.newLine();
                bw.flush();
            }
            logger.info("使用AES对文件加密结束，源文件地址[{}]加密后文件地址[{}]",orgFile.getPath(),encryptFile.getPath());
            return encryptFile;
        }catch (Exception e){
            logger.error("使用AES对文件加密异常,源文件地址[{}]加密后文件地址[{}]",orgFile.getPath(),encryptFile.getPath(),e);
            throw new Exception("使用AES对文件加密异常",e);
        }finally {
            CommonUtils.closeReaderandWriter(br,bw);
        }
    }

    //对文本解密，返回解密文件后的文件
    public static File decryptFile(File decryptfile,  File encryptFile,Map<String,Object> context) throws Exception {
        logger.info("使用AES对文件解密开始，源加密文件地址[{}]解密后文件地址[{}]",encryptFile.getPath(),decryptfile.getPath());
        BufferedReader br=null;
        BufferedWriter bw=null;
        try{
            if(decryptfile.exists()){
                decryptfile.delete();
            }
            //边读边加密边写
            br=new BufferedReader(new FileReader(encryptFile));
            bw=new BufferedWriter(new FileWriter(decryptfile));

            String len=null;
            String aesKey=null;
            //判断是否加密
            RSAPrivateKey privateKey= (RSAPrivateKey) context.get(Dirt.RSAPRIVATEKEY);
            if(null!=privateKey){
                StringBuffer sb=new StringBuffer();
                while ((len=br.readLine())!=null){
                    sb.append(len);
                    if(len.equals("\n")||len.equals("")||len.equals("\r\n")||len.equals("\r")){
                        aesKey=RSAUtils.decryptStr(privateKey,sb.toString());
                        break;
                    }
                }
            }
            if(null==aesKey){
                aesKey=(String) context.get(Dirt.AES_KEY);
            }
           logger.info("aesKey[{}]",aesKey);
            if(aesKey!=null){
                while ((len=br.readLine())!=null){
                    String decrypt= decryptStr(len,aesKey);
                    bw.write(decrypt);
                    bw.flush();
                    bw.newLine();
                }
            }
            logger.info("使用AES对文件解密结束，源加密文件地址[{}]解密后文件地址[{}]",encryptFile.getPath(),decryptfile.getPath());
            return decryptfile;
        }catch (Exception e){
            logger.error("使用AES对文件解密异常,源加密文件地址[{}]解密后文件地址[{}]",encryptFile.getPath(),decryptfile.getPath(),e);
            throw new Exception("使用AES对文件解密异常",e);
        }finally {
            CommonUtils.closeReaderandWriter(br,bw);
        }
    }
}
