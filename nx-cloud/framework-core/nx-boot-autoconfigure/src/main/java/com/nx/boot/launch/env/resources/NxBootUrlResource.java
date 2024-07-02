package com.nx.boot.launch.env.resources;

import com.nx.boot.launch.exception.NxBootResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import static com.nx.boot.launch.env.NxEnvironment.*;
import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

@Slf4j
public class NxBootUrlResource {

    public static  List<EncodedResource> getEncodedResource(ClassLoader classLoader ,String... coarsefileNames)  {
        if (coarsefileNames==null || coarsefileNames.length==0){
            return new ArrayList<>();
        }

        List<String> locationFiles = new ArrayList<>();
        Arrays.stream(coarsefileNames).forEach(coarseFileName->{
            String[]  coarseFileNameAry = coarseFileName.split(",");

            Arrays.stream(coarseFileNameAry).forEach(pathOrFileName->{
                if (pathOrFileName.endsWith("/")){
                    locationFiles.addAll( Arrays.stream(BOOT_FILE_NAME).map(filename->{return pathOrFileName + filename;}).collect(Collectors.toList()));
                    locationFiles.addAll( Arrays.stream(APPLICATION_FILE_NAME).map(filename->{return pathOrFileName + filename;}).collect(Collectors.toList()));
                }else{
                    locationFiles.add(pathOrFileName);
                }
            });
        });



        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader(classLoader);
        PathMatchingResourcePatternResolver resourcePatternResolver =  new PathMatchingResourcePatternResolver(classLoader);
        final List<EncodedResource> propertiesResources = new ArrayList<>(2);

        final List<EncodedResource> resourceList = new ArrayList<>(2);
        for (String location : locationFiles) {
            if (!location.startsWith("classpath*:")){
                try {
                    Resource[] resources = resourcePatternResolver.getResources(location);
                    for (Resource resource : resources) {
                        if (checkResource(resource)){
                            resourceList.add(new EncodedResource(resource));
                        }
                    }
                } catch (IOException e) {
                    throw new NxBootResourceException(e);
                }
            }else{
                Resource resource  = defaultResourceLoader.getResource(location);
                if (checkResource(resource)){
                    resourceList.add(new EncodedResource(resource));
                }
            }
        }

        return propertiesResources;
    }

    private static boolean checkResource(Resource resource){
        if (!resource.exists() || !resource.isFile()){
            try {
                log.warn(resource.getURL() + "不存在或不是一个文件");
            } catch (IOException e) {
                log.warn(resource.getFilename() + "不存在或不是一个文件");
                throw new RuntimeException(e);
            }
            return false;
        }
        return true;
    }

    private static Map<String, URL> getResourceUrlBySearchLocation(ClassLoader classLoader,String[] searchLocation, String... fileNames) {
        List<String> locationFiles = new ArrayList<>();
        Arrays.stream(searchLocation).forEach(location->{
            Arrays.stream(fileNames).forEach(fileName->{
                locationFiles.add(location + fileName);
            });
        });

        Map<String, URL> result = getResourceUrl(classLoader,locationFiles.toArray(new String[]{}));

        return result;
    }


    public static Map<String, URL> getDefaultNxBootResourceUrl(ClassLoader classLoader){
        Map<String, URL> result = getResourceUrlBySearchLocation(classLoader,new String[]{CLASSPATH_ALL_URL_PREFIX},DEFAULT_BOOT_FILE_NAME);
        return result;
    }


    public static Map<String, URL> getBootResourceUrl(ClassLoader classLoader){
        Map<String, URL> result = getResourceUrlBySearchLocation(classLoader,DEFAULT_SEARCH_LOCATIONS,BOOT_FILE_NAME);
        return result;
    }

    public static Map<String, URL> getSpringApplicationResourceUrl(ClassLoader classLoader){
        Map<String, URL> result =  getResourceUrlBySearchLocation(classLoader,DEFAULT_SEARCH_LOCATIONS,APPLICATION_FILE_NAME);
        return result;
    }




    /**
     * spring.config.location
     * 寻的关键字对应的文件位置
     * @param classLoader
     * @return
     */
    public static Map<String, URL> getResourceUrl(ClassLoader classLoader,String... fileNames)  {

        final List<EncodedResource> resourceList =  getEncodedResource(classLoader,fileNames);

        Map<String, URL> result = new HashMap();
        resourceList.stream().forEach(encodedResource->{
            Resource resource = encodedResource.getResource();
            try {
                if (checkResource(resource)){
                    result.put(encodedResource.getResource().getFilename(), encodedResource.getResource().getURL());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        return result;
    }


//    /**
//     * 相对位置：不以"/"开头，寻址起点为当前类所在的位置
//     * 绝对位置：以"/"开头，寻址起点为当前工程的classpath路径，maven工程中为target/classes目录
//     * 默认以classpath为寻址起点,故无需以"/"开头,使用绝对路径寻址
//     * 命令行
//     * @param
//     */
//    private static Set<String> getExtConfigLocation(final String key,String... defaultValue)  {
//        if (key.equalsIgnoreCase(KEY_PROFILES_ACTIVE) || key.equalsIgnoreCase(KEY_PROFILES_INCLUDE)) {
//            return null;
//        }
//        //optional
//        //classpath
//        List<String> list = cmdLineArgs.getOptionArgs().get(key);
//
//        if (list==null && list.size()==0 && allProperties.containsKey(key) && allProperties.get(key)!=null) {
//            String p = allProperties.getProperty(key).replace(";", ",");
//            list.addAll(Arrays.asList(p.split(",")));
//        }
//
//        if (list==null && list.size()==0 && System.getProperty(key) !=null) {
//            String p = System.getProperty(key).replace(";", ",");
//            list.addAll(Arrays.asList(p.split(",")));
//        }
//
//        if (list==null && list.size()==0 &&  System.getenv().get(key) !=null) {
//            String p = System.getenv().get(key).replace(";", ",");
//            list.addAll(Arrays.asList(p.split(",")));
//        }
//
//        if (list==null && list.size()==0 &&  System.getenv().get(key) !=null) {
//            list.addAll(Arrays.asList(defaultValue));
//        }
//
//        Set<String> myList = list.stream().map(item->{
//            if (!item.startsWith("classpath") && !item.startsWith("file")){
//                item = "file:"+item;
//            }
//            if (item.lastIndexOf(".")>0) {
//                return item;
//            }else  {
//                return item.endsWith("/")?item:item+"/";
//            }
//        }).collect(Collectors.toSet());
//        return myList;
//    }



}
