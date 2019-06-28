package cn.wangtao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FileController
 * @Auth 桃子
 * @Date 2019-6-26 17:05
 * @Version 1.0
 * @Description
 **/
@Controller
public class FileController {

    @RequestMapping("uploadFile")
    @ResponseBody
    public Map<String,String> uploadFile(@RequestParam("file") MultipartFile multipartFile,@RequestParam Map<String,String> context){
        Map<String,String> map=new HashMap<>();
        System.out.println("fileName: "+context.get("fileName"));
        System.out.println("OriginalFilename："+multipartFile.getOriginalFilename());

        //保存
        try {
            String transferFileName="C:\\Users\\boccfc\\Desktop\\自己\\test\\3.txt";
            multipartFile.transferTo(new File(transferFileName));
            System.out.println("transferFileName: "+transferFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.put("returnCode","000000");
        map.put("returnMsg","上传成功");
        return map;
    }



}
