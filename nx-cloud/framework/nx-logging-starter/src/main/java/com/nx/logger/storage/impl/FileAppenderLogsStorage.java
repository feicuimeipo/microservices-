package com.nx.logger.storage.impl;


import ch.qos.logback.core.util.FileUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.nx.logger.config.NxLoggerConfig;
import com.nx.logger.enums.NxLoggerType;
import com.nx.logger.storage.ILogStorage;
import com.nx.logger.storage.NxLoggerModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class FileAppenderLogsStorage implements ILogStorage {

    private NxLoggerConfig.FileLog config;
    private String fileName;
    private String ext = ".log";
    private String logPath;
    private NxLoggerType loggerType;

    public FileAppenderLogsStorage(NxLoggerConfig.FileLog config, String appId, String loggerName, String profile) {
        this.config = config;
        fileName = config.getFileName();
        logPath = config.getLogPath();
        if (!StringUtils.isEmpty(logPath)){
            logPath = "./logs/";
        }

        if (StringUtils.isNotEmpty(logPath)){
            if (logPath.startsWith("./")){
                logPath = logPath.substring(2);
            }else if(logPath.startsWith(".")){
                logPath = logPath.substring(1);
            }else if(logPath.startsWith("../")){
                File directory = new File("");
                String parent = directory.getParent();
                logPath = logPath.substring(3);
                logPath = parent.endsWith("/")?parent+logPath:parent+"/"+logPath;
            }
        }

        String ext = ".log";
        if (fileName.indexOf(".")>-1){
            ext = fileName.substring(fileName.indexOf("."));
            if (!ext.startsWith(".")){
                ext = "." + ext;
            }
            fileName = fileName.substring(0,fileName.indexOf("."));
        }


        if (fileName.endsWith("_")){
            fileName = fileName.substring(0,fileName.indexOf("_"));
        }

        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileName);
        if (loggerName==null){
            loggerName = loggerName.startsWith("_")?loggerName.substring(1):loggerName;
            if (!fileNames.contains(loggerName)){
                fileNames.add(loggerName);
            }
        }
        if (StringUtils.isNotEmpty(appId) && appId.indexOf("-")==-1){
            if (!fileNames.contains(appId)){
                fileNames.add(appId);
            }
        }
        if (StringUtils.isNotEmpty(profile) && profile.indexOf("-")==-1){
            if (!fileNames.contains(profile)){
                fileNames.add(profile);
            }
        }
        StringBuffer sb = new StringBuffer();
        for (String fileName : fileNames) {
            if (sb.length()>0){
                sb.append("_");
            }
            sb.append(fileName);
        }

        fileName = sb.toString();
        log.info(fileName);
    }



    private File createFile(NxLoggerType loggerType){

        if (!logPath.endsWith("/")) logPath += "/";

        if (loggerType!=null && !fileName.contains(loggerType.name())) {

            String name = StrUtil.toUnderlineCase(loggerType.name());

            fileName = fileName + "_" + name;
        }

        String date = LocalDateTimeUtil.format(LocalDateTime.now(),"yyyy-MM-dd");
        fileName = fileName + "_" + date;

        String fileFullName =  logPath + fileName + ext;
        File file =new File(fileFullName);//相对路径，如果没有前面的src，就在当前目录创建文件
        FileUtil.createMissingParentDirectories(file);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        return file;
    }


    @Override
    public void writeLog(NxLoggerModel dto){
        String content = JSONObject.toJSONString(dto);
        writeLog(content);
    }

    @Override
    public void writeLog(String content) {
        NxLoggerModel nxLoggerModel = null;
        try {
            if (content==null){
                return;
            }
            nxLoggerModel = JSONObject.parseObject(content, NxLoggerModel.class);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        if (nxLoggerModel == null) {
            return;
        }
        if (!content.endsWith("\r\n")){
            content +=  "\r\n";
        }
        File file = createFile(nxLoggerModel.getNxLoggerType());
        synchronized (file) {
            try (FileWriter fw = new FileWriter(file, true)) {
                fw.write(content);
                fw.flush();
            } catch (IOException e) {
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e1) {
                        log.error(e.getMessage(), e);
                    }
                    try (FileWriter fw = new FileWriter(file, true)) {
                        fw.write(content);
                        fw.flush();
                    } catch (IOException e1){}
                } else {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
