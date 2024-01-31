package com.jiangsir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description:   文件大小重复删除
 * @author 江Sir
 * Date:          2024/1/27 17:59
 * week:         星期六
 * ProjectName:   file-stream
 * FileName:      findAndRemoveDuplicateFiles
 */
public class FindAndRemoveDuplicateFiles {
    public static void main(String[] args) {
        String folderPath = "I:\\5T tease\\video";
        try {
            findAndRemoveDuplicateFiles(folderPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void findAndRemoveDuplicateFiles(String folderPath) throws IOException {
        Map<Long, File> fileMapBySize = new HashMap<>();
        traverseFolder(new File(folderPath), fileMapBySize);
    }

    private static void traverseFolder(File folder, Map<Long, File> fileMapBySize) throws IOException {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    long fileSize = file.length();

                    // 如果已经存在相同大小的文件，删除当前文件
                    if (fileMapBySize.containsKey(fileSize)) {
//                        Files.delete(file.toPath());
                        System.out.println("已删除文件: " + file.getAbsolutePath());
                    } else {
                        // 否则，将当前文件加入映射
                        fileMapBySize.put(fileSize, file);
                    }
                    System.out.println("fileSize = " + fileSize);
                    System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    // 递归遍历子文件夹
                    traverseFolder(file, fileMapBySize);
                }
            }
//            System.out.println("fileMapBySize = " + fileMapBySize);
        }
    }
//            private static void findAndRemoveDuplicateFiles(String folderPath) throws IOException {
//            Map<Long, File> fileMap = new LinkedHashMap<>();
//
//            File folder = new File(folderPath);
//            File[] files = folder.listFiles();
//
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isFile()) {
//                        long fileSize = file.length();
//                        if (fileMap.containsKey(fileSize)) {
//                            // 如果已经存在相同大小的文件，删除当前文件
////                            Files.delete(file.toPath());
//                            System.out.println("已删除文件: " + file.getAbsolutePath());
//                        } else {
//                            // 否则，将当前文件加入映射
//                            fileMap.put(fileSize, file);
//                        }
//                    }
//                }
//                System.out.println("fileMap = " + fileMap);
//            }
//        }

//    private static void findAndRemoveDuplicateFiles(String folderPath) throws IOException {
//        Map<String, Map<Long, File>> fileMapByExtension = new LinkedHashMap<>();
//
//        File folder = new File(folderPath);
//        File[] files = folder.listFiles();
//
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile()) {
//                    long fileSize = file.length();
//                    String fileExtension = getFileExtension(file.getName());
//
//                    if (fileExtension != null) {
//                        // 如果已经存在相同后缀名和大小的文件，删除当前文件
//                        if (fileMapByExtension.containsKey(fileExtension) && fileMapByExtension.get(fileExtension).containsKey(fileSize)) {
////                            Files.delete(file.toPath());
//                            System.out.println("已删除文件: " + file.getAbsolutePath());
//                        } else {
//                            // 否则，将当前文件加入映射
//                            fileMapByExtension
//                                    .computeIfAbsent(fileExtension, k -> new HashMap<>())
//                                    .put(fileSize, file);
//                        }
//                    }
//                }
//            }
//                System.out.println("fileMapByExtension = " + fileMapByExtension);
//        }
//    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }
}
