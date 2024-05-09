package com.jiangsir;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Description:   视频时长重复删除
 *
 * @Author: 江Sir
 * Date:          2024/1/27 18:22
 * week:         星期六
 * ProjectName:   file-stream
 * FileName:      FindAndRemoveDuplicateVideos
 */
public class FindAndRemoveDuplicateVideos {
    private final static String[] VIDEO_TYPE = {"mp4", "webm"};
    private final static String[] MUSIC_TYPE = {"mp3"};
    private final static String[] IMAGE_TYPE = {"jpg", "png", "jpeg"};
    //是否删除
    private static boolean IS_DEL = false;
    //文件夹地址
    private static String FOLDER_PATH = "I:\\5T\\video";

    /**
     * 判断输入的地址是否是文件夹
     *
     * @param folderPath 文件路径
     * @return 是否是文件夹
     */
    private static boolean isDirectory(String folderPath) {
        File file = new File(folderPath);
        // 这里可以添加自定义的条件判断，例如长度、特定值等
        return file.exists() && file.isDirectory();
    }


    // 计算图像的平均哈希值
    private static long calculateAverageHash(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        long totalValue = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                totalValue += getPixelValue(image.getRGB(x, y));
            }
        }

        return totalValue / ((long) width * height);
    }

    /**
     * 获取像素值的平均值
     */
    private static long getPixelValue(int pixel) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;

        return (red + green + blue) / 3;
    }

    public static void main(String[] args) {
        try {
            Object[] objects = null;

            // 定义一个标志，用于表示输入是否正确
            boolean validInput = false;
            while (!validInput) {
                objects = verifyParameterValid();
                String folderPath = (String) objects[0];
                //判断文件路径是否为文件夹
                if (isDirectory(folderPath)) {
                    boolean isDel = (boolean) objects[1];
                    Map<Long, File> videoMap = new HashMap<>();
                    Map<Long, File> imageMap = new HashMap<>();
                    Map<Long, File> imageHashMap = new HashMap<>();
                    Map<Long, File> fileSizeMap = new HashMap<>();
                    findAndRemoveDuplicateVideos(folderPath, videoMap, imageMap, imageHashMap, fileSizeMap, isDel, 0);
                    validInput = true;
                } else {
                    System.out.println("文件夹不存在！");
                    objects = verifyParameterValid();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取输入的文件夹和是否删除
     *
     * @return 输入数据
     */
    private static Object[] verifyParameterValid() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入文件夹路径: ");
        String filePath = scanner.nextLine();
        if ("".equals(filePath)) {
            System.out.println("默认文件夹路径是: " + FOLDER_PATH);
        } else {
            FOLDER_PATH = filePath;
        }
        System.out.print("是否查到重复删除(是/否)&(yes/No)&(true/false): ");
        String isDel = scanner.nextLine();
        if (isDel == null) {
            System.out.println("默认是否删除位: " + IS_DEL);
        } else if ("是".equalsIgnoreCase(isDel) || "yes".equalsIgnoreCase(isDel) || "true".equalsIgnoreCase(isDel)) {
            IS_DEL = true;
        }
        System.out.println("系统最终路径:" + FOLDER_PATH);
        System.out.println("系统最终删除状态:" + IS_DEL);
        return new Object[]{FOLDER_PATH, IS_DEL};
    }

    /**
     * @param folderPath  文件夹路径
     * @param videoMap    视频map
     * @param imageMap    图片map
     * @param fileSizeMap 文件大小map
     * @param isDel       是否删除
     * @throws IOException          异常
     * @throws InterruptedException 异常
     */
    private static void findAndRemoveDuplicateVideos(String folderPath, Map<Long, File> videoMap, Map<Long, File> imageMap, Map<Long, File> imageHashMap, Map<Long, File> fileSizeMap, Boolean isDel, int count) throws IOException, InterruptedException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        Connection conn = null;

        try {

            String url = "jdbc:mysql://127.0.0.1:3306/ls_zhongda";
            String sqlUser = "root";
            String sqlPwd = "La552157.@";
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, sqlUser, sqlPwd);
            String sql = "select * from sys_user where id = 0";
            CallableStatement callableStatement = conn.prepareCall(sql);
            System.out.println("callableStatement = " + callableStatement);
            System.out.println("Database connection established");
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) { /* ignore close errors */ }
            }
        }

        if (files != null) {
            for (File file : files) {
                //处理视频
                if (file.isFile() && isVideoFile(file)) {
                    //视频文件时长
                    long duration = getVideoDuration(file);
                    //文件没有时长
                    if (duration != -1) {
                        //文件路径
                        String filePath = file.getAbsolutePath();
                        //文件大小
                        long fileLength = file.length();
                        String fileName = file.getName();
//                        if (videoMap.containsKey(duration) && fileSizeMap.containsKey(length)) {
//                            //Map中的文件
//                            File file1 = videoMap.get(duration);
//                            //文件大小
//                            long length1 = file1.length();
//                            Long duration1 = getVideoDuration(file1);
//                            System.out.println("\033[35m重复视频名: " + file1.getAbsolutePath() + ",文件大小: " + length1 / 1024 / 1024 + "MB, 时长: " + duration1 + " 秒\033[35m");
//                            //视频长度获取
//
//                            //获取文件信息
//                            String output = getVideoInfo(file);
//                            System.out.println("VideoOutput = " + output);
//                            long bitrate = getVideoBitrate(file);
//                            long bitrate1 = getVideoBitrate(file1);
//                            System.out.println("bitrate = " + bitrate + " bitrate1 = " + bitrate1);
//                            System.out.println("\033[33m对象中视频: " + file.getAbsolutePath() + ",文件大小: " + length / 1024 / 1024 + "MB, 时长: " + duration + " 秒\033[33m");
//                            if (bitrate == bitrate1) {
//                                System.out.println("\033[31m已删除视频: " + file.getAbsolutePath() + ",文件大小: " + length / 1024 / 1024 + "MB, 时长: " + duration + " 秒\033[31m");
//                                //处理删除文件
//                                if (isDel) {
//                                    count++;
//                                    Files.delete(file.toPath());
//                                }
//                            }
//                        } else {


                    }
                } else if (file.isFile() && isMusicFile(file)) {
                    //获取文件信息
                    String output = getVideoInfo(file);
                    System.out.println("musicOutput = " + output);
                } else if (file.isFile() && isImageFile(file)) {
                    //文件大小
                    long length = file.length();
                    //处理文件
                    if (imageMap.containsKey(length)) {
                        System.out.println("\033[31m重复图片名: " + imageMap.get(length) + "\033[31m");
                        System.out.println("已删除图片: " + file.getAbsolutePath() + ",文件大小: " + length + "MB" + "是否删除：" + isDel);
                        //处理删除文件
                        if (isDel) {
                            Files.delete(file.toPath());
                        }
                    } else {
                        //读取图像文件并缩小为 8x8 大小
                        BufferedImage image = Thumbnails.of(file).size(8, 8).asBufferedImage();

                        // 计算图像的平均哈希值
                        long hash = calculateAverageHash(image);
                        System.out.println("hash = " + hash);
                        //添加到map中
//                        System.out.println("\033[37m图片文件名: " + file + "\033[37m");
                        imageMap.put(length, file);

                        if (imageHashMap.containsKey(hash)) {
                            File file1 = imageHashMap.get(hash);
                            System.out.println("file1 = " + file1);
                            System.out.println("数组存在！");
                        }
                        imageHashMap.put(hash, file);
                    }
                } else if (file.isDirectory()) {
                    findAndRemoveDuplicateVideos(file.getAbsolutePath(), videoMap, imageMap, imageHashMap, fileSizeMap, isDel, count);
                }
            }
//            System.out.println("\033[31mfileMap = " + videoMap.size() + "\033[31m");
//            System.out.println("\033[31mimageMap = " + imageMap.size() + "\033[31m");
//            System.out.println("\033[31mfileSizeMap = " + fileSizeMap.size() + "\033[31m");
//            System.out.println("\033[31mcount = " + count + "\033[31m");
        }
    }


    /**
     * 判断文件是否是视频
     *
     * @param file 文件
     * @return 是否是视频
     */
    private static boolean isVideoFile(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            String substring = fileName.substring(dotIndex + 1).toLowerCase();
            return Arrays.asList(VIDEO_TYPE).contains(substring);
        }
        return false;
    }


    /**
     * 判断文件是否是视频
     *
     * @param file 文件
     * @return 是否是视频
     */
    private static boolean isMusicFile(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            String substring = fileName.substring(dotIndex + 1).toLowerCase();
            return Arrays.asList(MUSIC_TYPE).contains(substring);
        }
        return false;
    }

    /**
     * 判断文件是否是图片
     *
     * @param file 文件
     * @return 是否是图片
     */
    private static boolean isImageFile(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            String substring = fileName.substring(dotIndex + 1).toLowerCase();
            return Arrays.asList(IMAGE_TYPE).contains(substring);
        }
        return false;
    }

    /**
     * 获取文件时长
     *
     * @param videoFile 视频文件
     * @return 文件时长
     * @throws IOException
     * @throws InterruptedException
     */
    private static long getVideoDuration(File videoFile) throws IOException, InterruptedException {
        //获取文件信息
        String output = getVideoInfo(videoFile);
//            System.out.println("output = " + output);
        // 查找包含时长信息的输出行
        int durationIndex = output.indexOf("Duration:");
        if (durationIndex != -1) {
            String durationInfo = output.substring(durationIndex + "Duration:".length(), output.indexOf(",", durationIndex));
            String[] timeComponents = durationInfo.trim().split(":");
            return Integer.parseInt(timeComponents[0]) * 3600L + Integer.parseInt(timeComponents[1]) * 60L + (long) Math.floor(Double.parseDouble(timeComponents[2]));
        } else {
            // 没有找到时长信息
            return -1;
        }
    }

    /**
     * 获取文件比特率
     *
     * @param videoFile 视频文件
     * @return 文件时长
     * @throws IOException
     * @throws InterruptedException
     */
    private static long getVideoBitrate(File videoFile) throws IOException, InterruptedException {
        //获取文件信息
        String output = getVideoInfo(videoFile);
        //输出比特率信息
        return output.indexOf("bitrate:");
    }

    private static String getVideoInfo(File videoFile) throws IOException, InterruptedException {
        // 使用 ProcessBuilder 明确指定环境变量
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", videoFile.getAbsolutePath());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 使用 BufferedReader 读取 FFmpeg 输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder outputBuilder = new StringBuilder();

            // 查找包含时长信息的输出行
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
                if (line.contains("Duration:")) {
                    break;
                }
            }
            process.waitFor();

            // 处理 FFmpeg 输出
            String output = outputBuilder.toString().trim();
            System.out.println("output = " + output);
            return output;
        }
    }
}
