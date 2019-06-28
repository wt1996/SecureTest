package cn.wangtao.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @ClassName CommonUtils
 * @Auth 桃子
 * @Date 2019-6-27 12:51
 * @Version 1.0
 * @Description
 **/
public class CommonUtils {

    private static final Logger logger= LoggerFactory.getLogger(CommonUtils.class);
    //编码方式
    public static final String CODE_TYPE = "UTF-8";

    //字符补全
    private static final String[] consult = new String[]{"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G"};

    //关流
    public static void closeReaderandWriter(Reader reader, Writer writer){
        if(writer!=null){
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("关闭输出流失败",e);
            }
        }
        if(reader!=null){
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("关闭输出流失败",e);
            }
        }
    }

    //将16进制转换为二进制
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    //将二进制转换成16进制
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    //补全字符
    public static String completionCodeFor16Bytes(String str) throws Exception {
        try{
            int num = str.getBytes(CODE_TYPE).length;
            int index = num%16;
            //进行加密内容补全操作, 加密内容应该为 16字节的倍数, 当不足16*n字节是进行补全, 差一位时 补全16+1位
            //补全字符 以 $ 开始,$后一位代表$后补全字符位数,之后全部以0进行补全;
            if(index != 0){
                StringBuffer sbBuffer = new StringBuffer(str);
                if(16-index == 1){
                    sbBuffer.append("$" + consult[16-1] + addStr(16-1-1));
                }else{
                    sbBuffer.append("$" + consult[16-index-1] + addStr(16-index-1-1));
                }
                str = sbBuffer.toString();
            }
            return str;
        }catch (Exception e){
            logger.error("使用AES加密前补全字符异常",e);
            throw new Exception("使用AES加密前补全字符异常",e);
        }
    }

    //追加字符
    public static String addStr(int num){
        StringBuffer sbBuffer = new StringBuffer("");
        for (int i = 0; i < num; i++) {
            sbBuffer.append("0");
        }
        return sbBuffer.toString();
    }

    //还原字符(进行字符判断)
    public static String resumeCodeOf16Bytes(String str) throws Exception{
        int indexOf = str.lastIndexOf("$");
        if(indexOf == -1){
            return str;
        }
        String trim = str.substring(indexOf+1,indexOf+2).trim();
        int num = 0;
        for (int i = 0; i < consult.length; i++) {
            if(trim.equals(consult[i])){
                num = i;
            }
        }
        if(num == 0){
            return str;
        }
        return str.substring(0,indexOf).trim();
    }

}
