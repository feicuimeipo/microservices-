package com.nx.dubbo.spimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * @ClassName YamlPropertySourceFactory
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/8 15:04
 * @Version 1.0
 **/
@Slf4j
class YamlPropertySourceFactory extends DefaultPropertySourceFactory  {
    protected static final String DEFAULT_SEARCH_LOCATIONS = "file:./config/,file:./,classpath:/config/,classpath:/,classpath*:/config/,classpath*:/";

    protected static ClassLoader classLoader;
    public static void setClassLoader(ClassLoader classLoader){
        if (YamlPropertySourceFactory.classLoader == null) {
            synchronized (YamlPropertySourceFactory.class) {
                if (YamlPropertySourceFactory.classLoader == null) {
                    YamlPropertySourceFactory.classLoader = classLoader;
                }
            }
        }
    }

    protected static ClassLoader getClassLoader(){
        return YamlPropertySourceFactory.classLoader;
    }

    //protected static String DEFAULT_DUBBO_CONFIG_FILE = "/META-INF/default-application-dubbo.yml";
    public static final String DUBBO_CONFIG_FILE = "application-dubbo.yml";


    protected static String[] resourceFile = new String[]{"application-dubbo.yml"};

    public static void setResoruceFiles(String[] toArray) {
        if (YamlPropertySourceFactory.resourceFile == null) {
            synchronized (YamlPropertySourceFactory.class) {
                if (YamlPropertySourceFactory.resourceFile == null) {
                    YamlPropertySourceFactory.resourceFile = toArray;
                }
            }
        }
    }

    @Override
    public PropertySource createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        if (encodedResource == null) {
            return super.createPropertySource(name, encodedResource);        }

        for (String fileName : resourceFile) {
            List<EncodedResource> encodedResources= getEncodedResource(fileName);
            if (encodedResources!=null && encodedResources.size()>0){
                for (EncodedResource resource : encodedResources) {
                    encodedResource = resource;
                    List<PropertySource<?>> sources = new YamlPropertySourceLoader().load(encodedResource.getResource().getFilename(), encodedResource.getResource());
                    if (sources!=null && sources.size()>0){
                        return sources.get(0);
                    }
                }
            }
        }

        encodedResource = getDefaultEncodedResource();

        List<PropertySource<?>> sources = new YamlPropertySourceLoader().load(encodedResource.getResource().getFilename(), encodedResource.getResource());

        return sources!=null && sources.size()>0?sources.get(0):null;

    }


    public static List<EncodedResource> getEncodedResource(String fileName){
        final List<EncodedResource> propertiesResources = new ArrayList<>(2);

        List<String> list = Arrays.asList(StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(DEFAULT_SEARCH_LOCATIONS)));
        list.stream().filter(i -> propertiesResources.size()==0).forEach(location -> {
            if (location.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)){
                for (String filename : resourceFile) {
                    DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
                    if (classLoader!=null) {
                        defaultResourceLoader.setClassLoader(classLoader);
                    }
                    Resource resource = defaultResourceLoader.getResource(filename);
                    if (resource.exists() && resource.isFile()) {
                        propertiesResources.add(new EncodedResource(resource));
                    }
                }
            }else{
                ResourcePatternResolver resourcePatternResolver =  new PathMatchingResourcePatternResolver();
                if (classLoader!=null) {
                    resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
                }
                try {
                    Resource[] resources = resourcePatternResolver.getResources(fileName);
                    for (Resource resource : resources) {
                        if (resource.exists() && resource.isFile()) {
                            propertiesResources.add(new EncodedResource(resource));
                        }
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });


        return propertiesResources;
    }

    public EncodedResource getDefaultEncodedResource(){
        InputStream is = YamlPropertySourceFactory.class.getResourceAsStream(DUBBO_CONFIG_FILE);
        InputStreamResource inputStreamResource = new InputStreamResource(is);
        return new EncodedResource(inputStreamResource);
    }




    public static void main(String[] args) throws IOException {
        InputStream is = YamlPropertySourceFactory.class.getResourceAsStream(DUBBO_CONFIG_FILE);
        InputStreamResource inputStreamResource = new InputStreamResource(is);
        EncodedResource encodedResource = new EncodedResource(inputStreamResource);
    }
}
