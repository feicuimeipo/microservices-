package com.nx.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *  记录日志
 * @author NXL
 */
@Slf4j
@Component
public class LogFilter implements GlobalFilter, Ordered {
    private static final SimpleDateFormat SIMPLEDATAFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATAFORMAT = new SimpleDateFormat("yyyy-MM-dd-HH");

    @Value("${logging.file.path:/log}")
    private String logfilePath;

    @Data
    static class LogInfo{
        String method;
        String body;
        String params;
        String url;
        String header;
        String result;
        String ip;
        long timestamp = System.currentTimeMillis();
        String date = SIMPLEDATAFORMAT.format(new Date());
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LogInfo logInfo = new LogInfo();
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String method = serverHttpRequest.getMethodValue().toUpperCase();
        logInfo.setMethod(method);
        logInfo.setHeader(serverHttpRequest.getHeaders().toString());
        logInfo.setUrl(serverHttpRequest.getURI().toString());


        if(StringUtils.isNotBlank(serverHttpRequest.getHeaders().getFirst("X-Real-IP")))
        {
            logInfo.setIp(serverHttpRequest.getHeaders().getFirst("X-Real-IP"));
        }
        else {
            logInfo.setIp("IP获取异常！");
        }
        if(!("GET".equals(method) || "DELETE".equals(method))) {
            String body = exchange.getAttributeOrDefault("cachedRequestBody", "");
            if(StringUtils.isNotBlank(body)) {
                logInfo.setBody(body);
            }
        }
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(serverHttpResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        DataBufferUtils.release(dataBuffer);
                        String resp = new String(content, StandardCharsets.UTF_8);
                        logInfo.setResult(resp);
                        new Thread(()->{writeAccessLog(logInfo);}).start();
                        byte[] uppedContent = new String(content, StandardCharsets.UTF_8).getBytes();
                        return bufferFactory.wrap(uppedContent);
                    }));
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -20;
    }

    @Async
    public void writeAccessLog(LogInfo logInfo) {
        String logFullFilename =  logfilePath + File.separator + "gateway-log-"+DATAFORMAT.format(new Date())+".log";
        File file = new File(logfilePath+File.separator + "gateway-log-"+DATAFORMAT.format(new Date())+".log");
        if(!file.getParentFile().exists()){
            try { file.getParentFile().mkdirs();
            }catch (Exception e){
                log.warn("创建访问日志路径失败.{}",e.getMessage(),e);
            }
        }
        if(!file.exists()){
            try {
                if (file.createNewFile()){
                    file.setWritable(true);
                }
            }catch (Exception e){
                log.warn("创建访问日志路径失败.{}",e.getMessage(),e);
            }
        }
        try(
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        ){
            // 写入信息
            bufferedWriter.write(JSONObject.toJSONString(logInfo) + "\r\n");
            bufferedWriter.flush();// 清空缓冲区
            bufferedWriter.close();// 关闭输出流
        } catch (FileNotFoundException e){
            log.error("文件不存在，请检查文件. {}", e.getMessage(),e);
        } catch (IOException e) {
            log.error("写访问日志到文件失败. {}", e.getMessage(),e);
        }
    }


    @Async
    public void writeAccessLog1(LogInfo logInfo) {
        String logFullFilename =  logfilePath + "/gateway-log-"+DATAFORMAT.format(new Date())+".log";
        Path path = Paths.get(logfilePath);
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (FileAlreadyExistsException e) {
            }catch (IOException e){
                log.warn("创建访问日志路径失败.{}",e.getMessage(),e);
            }

            Path logfile = Paths.get(logFullFilename);
            if (Files.notExists(logfile)) {
                try {
                    Files.createFile(path);
                } catch (FileAlreadyExistsException e) {
                }catch (IOException e){
                    log.warn("创建访问日志文件失败.{}",e.getMessage(),e);
                }
            }
        }

        int bufSize = 100;
        //读
        //File fin = new File(logFullFilename);
        //FileChannel fcin = new RandomAccessFile(fin, "r").getChannel();
        //ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);
        //readFileByLine(bufSize, fcin, rBuffer, null, wBuffer);


        File logfile = new File(logFullFilename);
        try{
            FileChannel fLogfile= new RandomAccessFile(logfile, "rws").getChannel();
            ByteBuffer wBuffer = ByteBuffer.allocateDirect(bufSize);

            StringBuffer strBuf = new StringBuffer("");
            String line = new String(strBuf.toString() + JSONObject.toJSONString(logInfo) + "\r\n");
            writeFileByLine(fLogfile, wBuffer, line);

            System.out.print("OK!!!");
        }catch (IOException e){
            log.warn("写日志失败.{}",e.getMessage(),e);
        }
    }

    public static void readFileByLine(int bufSize, FileChannel fcin, ByteBuffer rBuffer, FileChannel fcout, ByteBuffer wBuffer){
        String enterStr = "\n";
        try{
            byte[] bs = new byte[bufSize];

            int size = 0;
            StringBuffer strBuf = new StringBuffer("");
            //while((size = fcin.read(buffer)) != -1){
            while(fcin.read(rBuffer) != -1){
                int rSize = rBuffer.position();
                rBuffer.rewind();
                rBuffer.get(bs);
                rBuffer.clear();
                String tempString = new String(bs, 0, rSize);
                //System.out.print(tempString);
                //System.out.print("<200>");

                int fromIndex = 0;
                int endIndex = 0;
                while((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1){
                    String line = tempString.substring(fromIndex, endIndex);
                    line = new String(strBuf.toString() + line);
                    //System.out.print(line);
                    //System.out.print("</over/>");
                    //write to anthone file
                    writeFileByLine(fcout, wBuffer, line);

                    strBuf.delete(0, strBuf.length());
                    fromIndex = endIndex + 1;
                }
                if(rSize > tempString.length()){
                    strBuf.append(tempString.substring(fromIndex, tempString.length()));
                }else{
                    strBuf.append(tempString.substring(fromIndex, rSize));
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void writeFileByLine(FileChannel fcout, ByteBuffer wBuffer, String line){
        try {
            //write on file head
            //fcout.write(wBuffer.wrap(line.getBytes()));
            //wirte append file on foot
            fcout.write(wBuffer.wrap(line.getBytes()), fcout.size());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}