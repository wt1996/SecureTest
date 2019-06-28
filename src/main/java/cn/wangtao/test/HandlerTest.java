package cn.wangtao.test;

import cn.wangtao.common.DefaultFileUploadHandler;
import cn.wangtao.utils.AESUtils;
import cn.wangtao.utils.Dirt;
import cn.wangtao.utils.RSAUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HandlerTest
 * @Auth 桃子
 * @Date 2019-6-27 15:45
 * @Version 1.0
 * @Description
 **/
public class HandlerTest {
    @Test
    public void testExecute(){
       /* try {
            Map<String,Object> context=new HashMap<>();
            KeyPair keyPair = RSAUtils.getKey();
            context.put(Dirt.RSAPRIVATEKEY,keyPair.getPrivate());
            context.put(Dirt.RSAPUBLICKEY,keyPair.getPublic());
            context.put("orgFileName","C:\\Users\\boccfc\\Desktop\\自己\\test\\test.txt");
            String url="http://localhost:9090/uploadFile"; //不同商户提供不同的地址
            context.put(Dirt.UPLOADFILEURL,url);
            DefaultFileUploadHandler fileUploadHandler = new DefaultFileUploadHandler();
            fileUploadHandler.execute(context);


            //进行解密
            String encryptFileName="C:\\Users\\boccfc\\Desktop\\自己\\test\\encrypt\\test.txt";
            String decryptfileName="C:\\Users\\boccfc\\Desktop\\自己\\test\\decrypt\\test.txt";
            context.put(Dirt.RSAPRIVATEKEY,keyPair.getPrivate());
            context.put(Dirt.AES_KEY,fileUploadHandler.getAeskey());
            AESUtils.decryptFile(new File(decryptfileName),new File(encryptFileName),context);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
