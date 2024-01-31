package com.jiangsir;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Description:   视频时长重复删除
 *
 * @Author: 江Sir
 * Date:          2024/1/27 18:22
 * week:         星期六
 * ProjectName:   file-stream
 * FileName:      FindAndRemoveDuplicateVideos
 */
public class ImageDuplicateDetectionSSIM {
    private static final String[] IMAGE_TYPES = {"jpg", "png"};

    public static void main(String[] args) {
        String folderPath = "path/to/your/images";  // 替换为你的图像文件夹路径

        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                for (int j = i + 1; j < files.length; j++) {
                    if (isImageFile(files[i]) && isImageFile(files[j])) {
                        try {
                            BufferedImage image1 = Thumbnails.of(files[i]).size(256, 256).asBufferedImage();
                            BufferedImage image2 = Thumbnails.of(files[j]).size(256, 256).asBufferedImage();

                            // 使用 SSIM 指数比较图像相似度
                            double ssim =  0.0;//compareImagesWithSSIM(image1, image2);

                            // 设置一个阈值，根据实际需求调整
                            double threshold = 0.90;

                            if (ssim > threshold) {
                                System.out.println("相似度高于阈值，可能为重复图片:");
                                System.out.println("图片1: " + files[i].getAbsolutePath());
                                System.out.println("图片2: " + files[j].getAbsolutePath());
                                System.out.println("SSIM 相似度: " + ssim);
                                System.out.println();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        for (String type : IMAGE_TYPES) {
            if (fileName.endsWith("." + type)) {
                return true;
            }
        }
        return false;
    }

//    private static double compareImagesWithSSIM(BufferedImage image1, BufferedImage image2) {
//        HashingAlgorithm algorithm = new PerceptiveHash(32);
//
//        double hashDifference = algorithm.normalizedHammingDistance(image1, image2);
//
//        SsimMatcher ssmMatcher = new SsimMatcher(Mode.RGB);
//        return 1 - (ssmMatcher.getStructuralSimilarityIndex(image1, image2) * 0.5 + hashDifference * 0.5);
//    }
}
