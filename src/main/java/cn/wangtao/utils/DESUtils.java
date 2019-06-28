package cn.wangtao.utils;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @ClassName DESUtils
 * @Auth 桃子
 * @Date 2019-6-27 16:57
 * @Version 1.0
 * @Description
 **/
public class DESUtils {
    private static final Logger logger= LoggerFactory.getLogger(DESUtils.class);
    //编码方式
    public static final String CODE_TYPE = "UTF-8";
    //对字符串加密
    public static String encryptStr(String content,String aesKey) throws Exception {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(),"DES" );
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //字符补全
            String content16Str = CommonUtils.completionCodeFor16Bytes(content);
            byte[] encryptedData = cipher.doFinal(content16Str.getBytes(CODE_TYPE));
            //2进制转换成16进制
            String hexStr = CommonUtils.parseByte2HexStr(encryptedData);
            return hexStr;
        } catch (Exception e) {
            logger.error("使用AES对字符串加密异常",e);
            throw new Exception("使用AES对字符串加密异常",e);
        }
    }
    
    @Test
    public void test1() throws Exception {
        String s = encryptStr("1111", "test1234");
        System.out.println(s);
    }
}
