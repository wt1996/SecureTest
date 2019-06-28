package cn.wangtao.utils;

import cn.wangtao.common.DefaultFileUploadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName QuartzTasker
 * @Auth 桃子
 * @Date 2019-6-27 10:40
 * @Version 1.0
 * @Description
 **/
@EnableScheduling
@Configuration
public class QuartzTasker {

    private static final Logger logger= LoggerFactory.getLogger(QuartzTasker.class);

    private boolean ifTest=true;
    private int maxSendNum=3;
    //定时推送对账文件
    @Scheduled(cron = "*/20 * * * * ?")
    public void uploadFile(){
        logger.info("推送文件定时任务开始");
        DefaultFileUploadHandler fileUploadHandler = new DefaultFileUploadHandler();
        Map<String,Object> context=new HashMap<>();
        try {
            KeyPair keyPair = RSAUtils.getKey();
            context.put(Dirt.RSAPRIVATEKEY,keyPair.getPrivate());
            context.put(Dirt.RSAPUBLICKEY,keyPair.getPublic());
            context.put("orgFileName",new File("C:\\Users\\boccfc\\Desktop\\自己\\test\\test.txt"));
            String url="http://localhost:9090/uploadFile"; //不同商户提供不同的地址
            context.put(Dirt.UPLOADFILEURL,url);
            fileUploadHandler.execute(context);

            //测试解密
            if(ifTest){
                String encryptFileName="C:\\Users\\boccfc\\Desktop\\自己\\test\\encrypt\\test.txt";
                String decryptfileName="C:\\Users\\boccfc\\Desktop\\自己\\test\\decrypt\\test.txt";
                context.put(Dirt.RSAPRIVATEKEY,keyPair.getPrivate());
                context.put(Dirt.AES_KEY,fileUploadHandler.getAeskey());
                AESUtils.decryptFile(new File(decryptfileName),new File(encryptFileName),context);
            }
            logger.info("推送文件定时任务结束");
        } catch (Exception e) {
            logger.error("推送文件异常",e);
            while(--this.maxSendNum>0){
                logger.info("推送文件失败，尝试重复推送文件开始,还剩下[{}]机会",this.maxSendNum);
                try {
                     fileUploadHandler.execute(context);
                     if(Dirt.SUCCESSCODE.equals(context.get(Dirt.RETURNCODE))){
                         logger.info("推送文件成功，尝试重复推送文件成功");
                         return;
                     }
                } catch (Exception e1) {
                    logger.error("尝试重复推送文件异常",e1);
                    continue;
                }
            }
            logger.error("推送文件失败，尝试重复推送文件结束");
            logger.error("推送对账文件异常，开始发送短信通知");

            //短信通知

        }
    }
}
