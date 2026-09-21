package com.sky.controller.admin.common;


import com.sky.constant.MessageConstant;
import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.minio.PutObjectArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.minio.MinioClient;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "上传接口")
public class CommonController {
    //    @Autowired
//    private AliOssUtil aliOssUtil;
//
//    @Autowired
//    private AliOssProperties aliOssProperties;
    @Autowired
    private MinioClient minioClient;

    @Value("${sky.minio.bucket-name}")
    private String bucketName;

    @Value("${sky.minio.endpoint}")
    private String endpoint;

    //    @PostMapping("/upload")
//    @ApiOperation("文件上传")
//    public Result<String> upload(MultipartFile file) {
//        log.info("文件上传 ：{}", file);
//        String originalFilename = file.getOriginalFilename();
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String objectName = UUID.randomUUID().toString() + extension;
//
//        try {
//            BeanUtils.copyProperties(aliOssProperties, aliOssUtil);
//            String url = aliOssUtil.upload(file.getBytes(), objectName);
//            return Result.success(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.info("文件上传失败：{},e");
//        }
//        return Result.error(MessageConstant.UPLOAD_FAILED);
//    }
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {

        log.info("文件上传：{}", file.getOriginalFilename());

        try {
            // 1. 获取原始文件名
            String originalFilename = file.getOriginalFilename();

            // 2. 获取文件后缀
            String extension = "";

            if (originalFilename != null &&
                    originalFilename.contains(".")) {
                extension = originalFilename.substring(
                        originalFilename.lastIndexOf(".")
                );
            }

            // 3. 生成唯一文件名
            String objectName =
                    "dish/" + UUID.randomUUID() + extension;

            // 4. 上传到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1
                            )
                            .contentType(file.getContentType())
                            .build()
            );

            // 5. 拼接图片访问地址
            String url = endpoint
                    + "/" + bucketName
                    + "/" + objectName;

            log.info("文件上传成功：{}", url);

            return Result.success(url);

        } catch (Exception e) {

            log.error("文件上传失败", e);

            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
