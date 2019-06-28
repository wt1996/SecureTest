package cn.wangtao.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @ClassName HttpClientUtils
 * @Auth 桃子
 * @Date 2019-6-27 11:33
 * @Version 1.0
 * @Description
 **/
public class HttpClientUtils {

    private static final Logger logger= LoggerFactory.getLogger(HttpClientUtils.class);

    //上传文件
    public static Map uploadFile( File file,Map<String,String> params) throws Exception {
        String uploadFileUrl = params.get(Dirt.UPLOADFILEURL);
        logger.info("通过HTTPClient上传文件开始,上传地址：[{}]",uploadFileUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost=new HttpPost(uploadFileUrl);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();
        httpPost.setConfig(requestConfig);

        //添加文件信息
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));
        multipartEntityBuilder.addBinaryBody("file", file);

        //添加文本参数
        if (null != params && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName("UTF-8")));
            }
        }

        //准备执行
        HttpEntity httpEntity = multipartEntityBuilder.build();
        httpPost.setEntity(httpEntity);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            if(responseEntity!=null){
                ObjectMapper objectMapper=new ObjectMapper();
                Map map = objectMapper.readValue(responseEntity.getContent(), Map.class);
                logger.info("通过HTTPClient上传文件成功,接收返回结果：[{}]",map);
                return map;
            }
        } catch (IOException e) {
            logger.error("通过HTTPClient上传文件异常",e);
            throw  new Exception("通过HTTPClient上传文件异常",e);
        }
        return null;
    }
}
