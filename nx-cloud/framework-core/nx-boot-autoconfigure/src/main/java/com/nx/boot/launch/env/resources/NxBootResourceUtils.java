package com.nx.boot.launch.env.resources;

import com.nx.common.context.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 资源文件加载工具类
 * @Author nianxiaoling
 * @Author vakinge
 */
@Slf4j
public class NxBootResourceUtils extends ResourceUtils {
    private static String[]     excludes =new String[]{"i18n","pom.properties","bootstrap-","application-","application.","bootstrap."};
    private static String[]     fileExt = new String[]{".yaml",".yml",".properties"};

    private static String withProfileKeyFile = null;
    private static String profileFileBaseName = null;
    private static String activeProfileFile = null;



    static {
        //loadLocalConfig();
    }

//    public static void loadConfig(Class<?> source,String[] args) throws IOException {
//        if (classLoader!=null && rootPath!=null) return;
//        classLoader = source!=null? source.getClassLoader():
//                (Thread.currentThread().getContextClassLoader()==null? NxBootResourceUtils.class.getClassLoader():Thread.currentThread().getContextClassLoader());
//        rootPath = new File(classLoader.getResource("").getPath());
//        cmdLineArgs = CmdLineArgs.parse(args);
//
//
//        List<Object> keys = new ArrayList<>(allProperties.keySet());
//        loadLocalConfig(null,false);
//        Map<String,URL> classPathConfig = new HashMap<>();
//        classPathConfig.putAll(NxBootUrlResource.getDefaultNxBootResourceUrl(classLoader));
//        classPathConfig.putAll(NxBootUrlResource.getBootResourceUrl(classLoader));
//
//        if (StringUtils.isNotEmpty(cmdLineArgs.getOptionArgsAsString(KEY_CONFIG_LOCATION))){
//            String[] fileNames = cmdLineArgs.getOptionArgsAsString(KEY_CONFIG_LOCATION).split(",");
//            classPathConfig.putAll(NxBootUrlResource.getResourceUrl(classLoader,fileNames));
//        }else{
//            invokeApplicationName();
//            classPathConfig.putAll(NxBootUrlResource.getSpringApplicationResourceUrl(classLoader));
//
//            String[] fileNames = cmdLineArgs.getOptionArgsAsString(KEY_ADDITION_CONFIG_LOCATION).split(",");
//            classPathConfig.putAll(NxBootUrlResource.getResourceUrl(classLoader,fileNames));
//        }
//        loadLocalConfig(classPathConfig,true);
//
//
//        for (String key : cmdLineArgs.getOptionArgs().keySet()) {
//           allProperties.setProperty(key,collection2String(cmdLineArgs.getOptionArgs().get(key),","));
//        }
//
//        for (Object key : keys) {
//            if(key == null || allProperties.getProperty(key.toString()) == null)continue;
//            if(allProperties.getProperty(key.toString()).contains(PLACEHOLDER_PREFIX)){
//                String value = replaceRefValue(allProperties.getProperty(key.toString()));
//                if(StringUtils.isNotBlank(value))allProperties.setProperty(key.toString(), value);
//            }
//        }
//    }



    private  static URL getFileUrl(Optional<File> optional)  {
        return optional.isPresent() && optional==null?null:getFileUrl(optional.get());
    }
    private  static URL getFileUrl(File file)  {
        try {
            return new URL("file", "localhost", file.getPath());
        }catch (MalformedURLException e){
            log.error("转URL出错:file="+file.getPath(),e);
            return null;
        }
    }


    public static void loadLocalConfig(ClassLoader classLoader,Map<String, URL> urls, boolean specifyUrl) throws IOException {
        if (specifyUrl && (urls==null || urls.size()==0)) return;;
        if (!specifyUrl){
            urls = new HashMap<>();
            URL pathURl = classLoader.getResource("")==null? NxBootResourceUtils.class.getResource(""):classLoader.getResource("");
            urls.put("null",pathURl);
        }
        //Set<String> fileNames = urls.keySet();

        for (String fileKey : urls.keySet()) {
            String fileName = fileKey.equals("null")?null:fileKey;
            URL pathURl = urls.get(fileKey);
            Map<String, List<String>> allFileMap = new HashMap<>();
            if (pathURl!=null) {
                if (pathURl.getProtocol().equals("file")) {
                    log.info(">>loadPropertiesFromFile,origin:" + pathURl.getPath());
                    File parent = new File(pathURl.getPath());
                    if (!parent.exists()) {
                        log.error(">>loadPropertiesFromFile_error,dir not found");
                    } else {
                        loadPropertiesFromFile(parent, allFileMap,specifyUrl,fileName);
                        Set<String> fileExts = allFileMap.keySet();
                        for (String key : fileExts) {
                            parseConfigSortFiles(allFileMap.get(key), key, null);
                        }
                    }
                } else if (pathURl.getProtocol().equals("jar")) {
                    loadPropertiesFromJarFile(pathURl, allFileMap,specifyUrl,fileName);
                }
            }

            //加载外部文件
            if (!specifyUrl) {
                String extConfigLocationKey = "nx.ext.config.location";
                String[] locations = System.getProperty(extConfigLocationKey) != null ? System.getProperty(extConfigLocationKey).replace(";", ",").split(",") : new String[]{};
                for (String location : locations) {
                    File file = new File(location);
                    if (file.exists()) {
                        loadPropertiesFromFile(file, allFileMap, false,fileName);
                    }
                }
            }
        }


    }



    /**
     * 指定文件
     * @param url
     * @param allFileMap
     * @param fileNames
     * @throws IOException
     */
    private static void loadPropertiesFromJarFile(URL url,Map<String, List<String>> allFileMap,boolean specialFile,String... fileNames) throws  IOException {
        log.info(">>loadPropertiesFromJarFile,origin:" + url.toString());
        String jarFilePath = url.getFile();
        if(jarFilePath.contains("war!")){
            jarFilePath = StringUtils.splitByWholeSeparator(jarFilePath, "war!")[0] + "war";
        }else if(jarFilePath.contains("jar!")){
            jarFilePath = StringUtils.splitByWholeSeparator(jarFilePath, "jar!")[0] + "jar";
        }
        jarFilePath = jarFilePath.substring("file:".length());
        jarFilePath = java.net.URLDecoder.decode(jarFilePath, "UTF-8");
        log.info(">>loadPropertiesFromJarFile,real:" + jarFilePath);
        JarFile jarFile = new JarFile(jarFilePath);

        String fileExt = null;
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            fileExt = entry.getName().substring(entry.getName().lastIndexOf("."));
            if (specialFile) {
                boolean exist = Arrays.stream(fileNames).filter(name -> entry.getName().endsWith(name)).findFirst().isPresent();
                if (!exist){
                    continue;
                }
            }
            if (!specialFile && !checkEntryName(entry.getName())) {
                continue;
            }

            fileExt = entry.getName().substring(entry.getName().lastIndexOf("."));
            if(!allFileMap.containsKey(fileExt)){
                allFileMap.put(fileExt, new ArrayList<>());
            }
            allFileMap.get(fileExt).add(entry.getName());

        }

        Set<String> fileExts = allFileMap.keySet();
        for (String key : fileExts) {
            parseConfigSortFiles(allFileMap.get(key), key, jarFile);
        }

        jarFile.close();
    }

    private static void loadPropertiesFromFile(File parent,Map<String, List<String>> allFileMap,boolean specialFile,String... fileNames){
        File[] files = parent.listFiles();
        if(files == null)return;
        String fileExt = null;
        for (File file : files) {
            if (specialFile) {
                boolean exist = Arrays.stream(fileNames).filter(name -> file.getName().endsWith(name)).findFirst().isPresent();
                if (!exist){
                    continue;
                }
            }
            if (file.isDirectory()) {
                loadPropertiesFromFile(file, allFileMap, specialFile,fileNames);
            } else {
                String path = file.getPath();
                if (!specialFile && !checkEntryName(path)) {
                     continue;
                }
                fileExt = path.substring(path.lastIndexOf("."));
                if (!allFileMap.containsKey(fileExt)) {
                    allFileMap.put(fileExt, new ArrayList<>());
                }
                allFileMap.get(fileExt).add(path);

            }

        }
    }

    private static boolean checkEntryName(String entryName){
        for (String exclude : excludes) {
            if (entryName.contains(exclude)) {
                return false;
            }
        }
        boolean isTrue = false;
        for (String ext : fileExt) {
            if (entryName.endsWith(ext)){
                isTrue = true;
            }
        }
        return isTrue;
    }







    public static void main(String[] args) {
        File file = new File("D:\\software\\session_xsh.xts");
        System.out.println(file.getParent());

        File file1 = new File("D:\\software");
        System.out.println(file1.getPath());

    }






    /**
     * @param fileList
     * @param fileExt
     * @throws IOException
     * @throws FileNotFoundException
     */
    protected static void parseConfigSortFiles(List<String> fileList, String fileExt,JarFile jarFile)
            throws IOException, FileNotFoundException {
        if(fileList.size() == 1){
            Properties p = parseToProperties(fileList.get(0), jarFile);
            allProperties.putAll(p);
            log.info(">>load properties from file:" + fileList.get(0));
        }else if(fileList.size() > 1){
            sortFileNames(fileList, fileExt);
            Map<String, Properties> filePropMap = new LinkedHashMap<>(fileList.size());

            Properties p;
            for (String file : fileList) {
                p = parseToProperties(file, jarFile);
                if(withProfileKeyFile ==null && p.containsKey("spring.profiles.active")){
                    withProfileKeyFile = file;
                    profileFileBaseName = file.replace(fileExt, "") + "-";
                    String profile = replaceRefValue(p.getProperty("spring.profiles.active"));
                    activeProfileFile = profileFileBaseName + profile + fileExt;
                    log.info(">>activeProfileFile:"+profile);
                }
                if(withProfileKeyFile ==null && p.containsKey("spring.profiles.include")){
                    withProfileKeyFile = file;
                    profileFileBaseName = file.replace(fileExt, "") + "-";
                    String profile = replaceRefValue(p.getProperty("spring.profiles.include"));
                    activeProfileFile = profileFileBaseName + profile + fileExt;
                    log.info(">>activeProfileFile:"+profile);
                }
                filePropMap.put(file, p);
            }

            for (String filePath : filePropMap.keySet()) {
                if(profileFileBaseName == null
                        || filePath.equals(withProfileKeyFile)
                        || !filePath.startsWith(profileFileBaseName)
                        || filePath.equals(activeProfileFile)){
                    allProperties.putAll(filePropMap.get(filePath));
                    log.info(">>load properties from file:" + filePath);
                }
            }
        }
    }

    private static Properties parseToProperties(String path,JarFile jarFile) throws FileNotFoundException, IOException{
        Properties properties = new Properties();
        Yaml yaml = null;
        if(path.endsWith(".yaml") || path.endsWith(".yml")){
            yaml = new Yaml();
        }
        if(jarFile == null){
            FileReader fileReader = new FileReader(path);
            if(yaml == null){
                properties.load(fileReader);
            }else{
                Map<String, Object> map = yaml.load(fileReader);
                parseYamlInnerMap(null, properties, map);
            }
            try {fileReader.close();} catch (Exception e) {}
        }else{
            try(InputStream inputStream = jarFile.getInputStream(jarFile.getJarEntry(path));){
                properties = parseToProperties(inputStream,yaml == null?false:true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return properties;
    }

    protected static Properties parseToProperties(InputStream inputStream,boolean isYaml) throws IOException{
        Properties properties = new Properties();
        if(!isYaml){
            properties.load(inputStream);
        }else{
            Yaml    yaml = new Yaml();
            Map<String, Object> map = yaml.load(inputStream);
            parseYamlInnerMap(null, properties, map);
        }
        return properties;
    }


    private static void sortFileNames(List<String> files,String ext){
        Collections.sort(files, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                o1 = o1.replace(ext, "");
                o2 = o2.replace(ext, "");
                return o1.compareTo(o2);
            }
        });
    }

    private static void parseYamlInnerMap(String keyPrefix,Properties result,Map<String, Object> yamlData){
        Object value;
        String currentKey;
        for (Object key : yamlData.keySet()) {
            currentKey = keyPrefix == null ? key.toString() : keyPrefix + "." + key.toString();
            value = yamlData.get(key);
            if(value instanceof Map){
                parseYamlInnerMap(currentKey, result, (Map)value);
            }else{
                result.put(currentKey, value);
            }
        }

    }

}
