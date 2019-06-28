package cn.wangtao.common;

import cn.wangtao.utils.AESUtils;
import cn.wangtao.utils.Dirt;
import cn.wangtao.utils.HttpClientUtils;
import cn.wangtao.utils.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AbstractFileUploadHandler
 * @Auth 桃子
 * @Date 2019-6-27 11:11
 * @Version 1.0
 * @Description
 **/
public abstract class AbstractFileUploadHandler {

    private static final Logger logger= LoggerFactory.getLogger(AbstractFileUploadHandler.class);
    //AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即8bytes。
    private final  String defaultAesKey="1111222233334444";//16位

    public String getAeskey(){
        return this.defaultAesKey;
    }

    //执行方法
    public  Map<String,Object> execute(Map<String,Object> context) throws Exception {

        if(null ==context){
            logger.info("推送文件失败，context为空");
            return context;
        }
        this.beforeExecute(context);
        //获取源文件
        File orgFile = this.getOrgFile(context);
        //对源文件加密
        File encryptFile =this.getEncryptFile(orgFile,context);
        //上传文件
        this.uploadFile(encryptFile,context);
        //执行后补充
        this.afterExecute(context);

        return context;
    }


    //参数校验
    protected void beforeExecute(Map<String, Object> context) {
        logger.info("推送加密文件前参数校验开始");

        //根据merchantId查询商户记录，获取URL，并添加到context中

        //执行的日期是yyyyMMHHmmss，根据执行的执行设置源文件的名称

        //RSA 的公钥信息判断是否存在，不存在设置为默认的对象


        //获取AESKEY ，如果没有为默认
        String aesKey = (String) context.get(Dirt.AES_KEY);
        if(null==aesKey){
            context.put(Dirt.AES_KEY, this.defaultAesKey);
        }
        logger.info("推送加密文件前参数校验结束");
    }

    //获取源文件
    protected File getOrgFile(Map<String,Object> context){
        logger.info("推送加密文件获取源文件开始");
        File orgFile =(File) context.get(Dirt.ORGFILENAME); //测试
        //可能是一个File集合，可以用于遍历获取称为集合
        context.put(Dirt.ORGFILENAME,orgFile);
        logger.info("推送加密文件获取源文件结束");
        return orgFile;
    }


    //对源文件加密
    protected File getEncryptFile(File orgFile,Map<String,Object> context) throws Exception {
        logger.info("推送加密文件对源文件加密开始");
        //加密文件名设置
        String encryptDirFileName=Dirt.BASELOCALDIR+File.separator+Dirt.ENCRYPTLOCALDIR;

        File encryptDirFile = new File(encryptDirFileName);
        if(!encryptDirFile.isDirectory()){
            encryptDirFile.mkdir();
        }
        String encryptFileName=encryptDirFileName+File.separator+orgFile.getName();
        File encryptFile =new File(encryptFileName);
        //加密文件补充
        this.extraEncryptFile(encryptFile, context);
        //加密核心内容
        encryptFile= AESUtils.encryptFile(orgFile, encryptFile, context);
        context.put(Dirt.ENCRYPTFILE,encryptFile);
        logger.info("推送加密文件对源文件加密结束");
        return encryptFile;
    }


    //上传文件
    protected void uploadFile(File uploadFile,Map<String,Object> context) throws Exception {
        logger.info("推送加密文件HttpClient上传开始");
        //准备参数
        Map<String, String> params = this.beforeSendExecute(context);
        //执行请求
        Map map = HttpClientUtils.uploadFile(uploadFile, params);
        context.putAll(map);
        logger.info("推送加密文件HttpClient上传结束");

        String returnCode=(String)context.get(Dirt.RETURNCODE);
        String returnMsg=(String)context.get(Dirt.RETURNMSG);
        if(Dirt.SUCCESSCODE.equals(returnCode)){
            logger.info("推送文件成功，返回码[{}]返回信息[{}]",returnCode,returnMsg);
        }else{
            logger.info("推送文件失败，返回码[{}]返回信息[{}]",returnCode,returnMsg);
            throw new Exception("推送文件失败");
        }
    }

    //准备文本参数，用于请求商户时使用
    protected Map<String,String> beforeSendExecute(Map<String,Object> context){
        logger.info("推送加密文件HttpClient上传文本参数准备开始");
        Map<String,String> params=new HashMap<>();
        params.put(Dirt.UPLOADFILEURL,(String)context.get(Dirt.UPLOADFILEURL));
        //待补充...
        logger.info("推送加密文件HttpClient上传文本参数准备结束");
        return params;
    }

    //执行后补充
    protected  void afterExecute(Map<String,Object> context) throws Exception{

    }

    //补充加密
    protected File extraEncryptFile(File encryptFile,Map<String,Object> context) throws Exception {
        logger.info("对加密文件进行补充加密开始");
        //清空文件
        if(encryptFile.exists()){
            encryptFile.delete();
        }
        String aesKey =(String)context.get(Dirt.AES_KEY);
        RSAPublicKey publicKey = (RSAPublicKey) context.get(Dirt.RSAPUBLICKEY);
        //默认是把AES秘钥，使用RSA方式加到文件第一行
        BufferedWriter bw=new BufferedWriter(new FileWriter(encryptFile));
        //写入秘钥加密之后放入第一行
        String encrypt= RSAUtils.encryptStr(publicKey,aesKey);
        bw.write(encrypt);
        bw.flush();
        bw.newLine();
        bw.newLine();
        context.put(Dirt.BUFFEREDWRITER,bw);
        logger.info("对加密文件进行补充加密结束");
        return encryptFile;
    }
}
