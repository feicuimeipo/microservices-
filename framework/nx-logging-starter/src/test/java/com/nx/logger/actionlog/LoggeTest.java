package com.nx.logger.actionlog;

import com.nx.logger.NxLoggerStorageProvider;
import com.nx.logger.model.api.dto.BuryingPointLogDTO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;


/**
 * @ClassName LoggerUtilsTest
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/5/31 19:00
 * @Version 1.0
 **/
public class LoggeTest {

    @Test
    public void info() throws IOException {

//
//        URL url = NxLoggerStorageProvider.class.getResource("/");
//        String fileFullName = url.getPath() + File.separator + "log.info";
//
//        File file =new File(fileFullName);//相对路径，如果没有前面的src，就在当前目录创建文件
//        if(!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        BuryingPointLogDTO dto = new BuryingPointLogDTO();
        dto.setAccsTime(new Date().getTime());
        NxLoggerStorageProvider.writeLog(dto);
        //NxLoggerStorageProvider.writeLog("a","b",100l,"d","{'json:':'json'}");
    }
}
