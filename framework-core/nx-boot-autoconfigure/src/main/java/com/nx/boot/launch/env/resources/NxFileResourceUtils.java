package com.nx.boot.launch.env.resources;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class NxFileResourceUtils {
    /**
     * 实现一级通配符
     * @param parent
     * @param configPath
     * @return
     */
    protected static List<File> getAbsolutePath(File parent, String configPath) {
        if (configPath==null){
            throw new RuntimeException("configPath为空或路径为以file:开头");
        }
        if (parent == null || !parent.exists() || parent.isFile()) {
            throw new RuntimeException("parent路不存在或不是文件！");
        }
        if (configPath.startsWith("file:")) {
            configPath = configPath.substring(configPath.indexOf(":") + 1);
        }
        configPath = configPath.replace("//", "/");

        //绝对路径
        if (configPath.startsWith("/") || configPath.indexOf(":") > -1) {
            if (configPath.contains("/*")) {
                return getWildcardChildPath(configPath);
            } else {
                File file = new File(configPath);
                if (file.exists()) {
                    return Arrays.asList(file);
                } else {
                    log.error("[" + configPath + "]不存在！");
                }
            }
        }

        String rootPath = "";
        if (configPath.startsWith("./")) {
            configPath = configPath.substring(2);
            rootPath = parent.getPath() + File.separator + configPath;                ;
        } else {
            while (configPath.indexOf("../") != -1) {
                parent = parent.getParentFile();
                configPath = configPath.substring("../".length());
            }
            rootPath = parent.getPath() + File.separator + configPath;
        }

        if (rootPath.contains("/*")) {
            return getWildcardChildPath(rootPath);
        }
        File file = new File(rootPath);
        if (file.exists()) {
            return Arrays.asList(file);
        } else {
            log.error("[" + configPath + "]不存在！");
        }
        return new ArrayList<>();
    }

    private static List<File> getWildcardChildPath(String rootPath) {
        String start = rootPath.substring(0, rootPath.indexOf("/*") - 1);
        String end = rootPath.endsWith("/*") ? "" : rootPath.substring(0, rootPath.indexOf("/*") + 2);
        end = end.replace("\\*", "");
        end = end.replace("//", "/");
        String suffix = end;
        File child = new File(start);
        List<File> paths = Arrays.stream(child.listFiles()).filter(File::isDirectory).map(item -> {
            return new File(item.getPath() + File.separator + suffix);
        }).collect(Collectors.toList());
        return paths;
    }


}
