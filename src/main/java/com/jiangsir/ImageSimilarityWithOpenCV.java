package com.jiangsir;

import java.io.File;
import java.util.Arrays;

/**
 * Description:   图片处理
 *
 * @Author: 江Sir
 * Date:          2024/1/28 16:41
 * week:         星期日
 * ProjectName:   file-stream
 * FileName:      ImageSimilarityWithOpenCV
 */
public class ImageSimilarityWithOpenCV {
//    private static final String[] IMAGE_TYPES = {"jpg", "png"};
//
//    public static void main(String[] args) {
//        String folderPath = "path/to/your/images";  // 替换为你的图像文件夹路径
//
//        File folder = new File(folderPath);
//        File[] files = folder.listFiles();
//
//        if (files != null) {
//            for (int i = 0; i < files.length; i++) {
//                for (int j = i + 1; j < files.length; j++) {
//                    if (isImageFile(files[i]) && isImageFile(files[j])) {
//                        try {
//                            Mat img1 = Imgcodecs.imread(files[i].getAbsolutePath());
//                            Mat img2 = Imgcodecs.imread(files[j].getAbsolutePath());
//
//                            // 比较图像相似度
//                            double similarity = compareImagesWithOpenCV(img1, img2);
//
//                            // 设置一个阈值，根据实际需求调整
//                            double threshold = 0.90;
//
//                            if (similarity > threshold) {
//                                System.out.println("相似度高于阈值，可能为重复图片:");
//                                System.out.println("图片1: " + files[i].getAbsolutePath());
//                                System.out.println("图片2: " + files[j].getAbsolutePath());
//                                System.out.println("相似度: " + similarity);
//                                System.out.println();
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private static boolean isImageFile(File file) {
//        String fileName = file.getName().toLowerCase();
//        for (String type : IMAGE_TYPES) {
//            if (fileName.endsWith("." + type)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static double compareImagesWithOpenCV(Mat img1, Mat img2) {
//        // 转换图像为灰度
//        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2GRAY);
//
//        // 调整图像大小
//        Size size = new Size(256, 256);
//        Imgproc.resize(img1, img1, size);
//        Imgproc.resize(img2, img2, size);
//
//        // 计算直方图
//        MatOfInt histSize = new MatOfInt(256);
//        MatOfFloat ranges = new MatOfFloat(0f, 256f);
//
//        Imgproc.calcHist(Arrays.asList(img1), new MatOfInt(0), new Mat(), img1, histSize, ranges);
//        Imgproc.calcHist(Arrays.asList(img2), new MatOfInt(0), new Mat(), img2, histSize, ranges);
//
//        // 比较直方图
//        return Imgproc.compareHist(img1, img2, Imgproc.CV_COMP_CORREL);
//    }

}
