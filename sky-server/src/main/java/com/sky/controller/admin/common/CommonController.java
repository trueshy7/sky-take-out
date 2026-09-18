package com.sky.controller.admin.common;


import com.sky.constant.MessageConstant;
import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "上传接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private AliOssProperties aliOssProperties;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传 ：{}",file);
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString() + extension;

        try {
            BeanUtils.copyProperties(aliOssProperties,aliOssUtil);
            String url = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(url);

        } catch (IOException e) {
            e.printStackTrace();
            log.info("文件上传失败：{},e");
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
