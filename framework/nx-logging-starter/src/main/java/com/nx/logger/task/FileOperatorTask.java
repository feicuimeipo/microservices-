package com.nx.logger.task;

import com.nx.logger.storage.impl.FileAppenderLogsStorage;
import com.nx.logger.task.core.GeneralTask;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class FileOperatorTask extends GeneralTask {
    public static void backup(String path,int maxFileSize) {

        File filePath=new File(path);
        if (filePath.isFile()){
            log.error("必须是文件夹！");
            return;
        }
        File[] listFiles=filePath.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) continue;
            if (file.isFile()){
                synchronized (file) {
                    long kb = file.length() / 1024;
                    long mb = kb / 1024;
                    if (mb >= maxFileSize) {
                        //备分
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");
                        String oriFileFullName = path + File.separator + file.getName();
                        String targetFileFullName = path + File.separator + file.getName() + "." + df.format(new Date());
                        if (new File(targetFileFullName).exists()) {
                            continue;
                        }
                        copyFile(oriFileFullName, targetFileFullName);
                        file.delete();
                        try {
                            File newFile = new File(oriFileFullName);
                            newFile.createNewFile();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
    }


    public static void copyFile(String oriFile, String targetFile) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(oriFile);
            os = new FileOutputStream(targetFile);
            byte[] data = new byte[1024];//缓存容器
            int len = -1;//接收长度
            while((len=is.read(data))!=-1) {
                os.write(data, 0, len);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            //	释放资源 分别关闭 先打开的后关闭
            try {
                if(null!=os) {
                    os.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            try {
                if(null!=is) {
                    is.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }

    public static void copyDir(String srcPath,String destPath) {
        File src = new File(srcPath);//源头
        File dest = new File(destPath);//目的地
        //判断是否为目录，不存在则不作操作
              /*if(!src.isDirectory()) {
                  return;
              }*/
        //判断目的地目录是否存在，不存在就创建目录
        if(!dest.exists()) {
            boolean mkdir = dest.mkdirs();
        }
        //获取源头目录下的文件列表，每个对象代表一个目录或者文件
        File[] srcList = src.listFiles();
        if (null != srcList && srcList.length > 0){
            //遍历源头目录下的文件列表
            for (File aSrcList : srcList) {
                //如果是目录的话
                if (aSrcList.isDirectory()) {
                    //递归调用复制该目录
                    copyDir(srcPath + File.separator + aSrcList.getName(), destPath + File.separator + aSrcList.getName());
                    //如果是文件的话
                } else if (aSrcList.isFile()) {
                    //递归复制该文件
                    copyFile(srcPath + File.separator + aSrcList.getName(), destPath + File.separator + aSrcList.getName());
                }
                //删除原文件
                //aSrcList.delete();
            }
        }
//      boolean delete = src.delete();
    }

    /*
     * 计算两个时间之间相隔天数
     * @param startday 开始时间
     * @param endday 结束时间
     * @return
     */
    public int getIntervalDays(Calendar startday, Calendar endday){
        //确保startday在endday之前
        if(startday.after(endday)){
            Calendar cal=startday;
            startday=endday;
            endday=cal;
        }
        //分别得到两个时间的毫秒数
        long sl=startday.getTimeInMillis();
        long el=endday.getTimeInMillis();
        long ei=el-sl;
        //根据毫秒数计算间隔天数
        return (int)(ei/(1000*60*60*24));
    }


    /**
     * 定时删除
     */
    public void removeFiles(String filepath,int maxHistoryDay) {

        File filePath=new File(filepath);
        if (filePath.isFile()){
            log.error("必须是文件夹！");
            return;
        }
        if (!filePath.exists()) return;;
        File[] listFiles=filePath.listFiles();
        for (File file : listFiles) {
            if (file.exists() && file.isFile()){
                int day = getIntervalDays(new Date(),getCreationTime(file));
                if (day >maxHistoryDay){
                    //TODO: 清除阶建时间 > 的文件
                    file.delete();
                }
            }

        }
    }


    public int getIntervalDays(Date startday,Date endday){
        //确保startday在endday之前
        if(startday.after(endday)){
            Date cal=startday;
            startday=endday;
            endday=cal;
        }
        //分别得到两个时间的毫秒数
        long sl=startday.getTime();
        long el=endday.getTime();
        long ei=el-sl;
        //根据毫秒数计算间隔天数
        return (int)(ei/(10006060*24));
    }



    public static Date getCreationTime(File file) {
        if (file == null) {
            return null;
        }

        BasicFileAttributes attr = null;
        try {
            Path path =  file.toPath();
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建时间

        Instant instant = attr.creationTime().toInstant();
        // 更新时间
        //Instant instant = attr.lastModifiedTime().toInstant();
        // 上次访问时间
        //Instant instant = attr.lastAccessTime().toInstant();
        String format = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(instant);

        return new Date(attr.creationTime().toMillis());
    }


    @Override
    public void doRun() {

    }

    public static void main(String[] args) {
        FileOperatorTask fileOperatorTask = new FileOperatorTask();
        fileOperatorTask.backup("D:\\opt\\docs\\skywalking",0);
    }
}
