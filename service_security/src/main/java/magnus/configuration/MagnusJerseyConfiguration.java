package magnus.configuration;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Objects;
import java.util.stream.Collectors;

//@ApplicationPath("/restful")
public class MagnusJerseyConfiguration extends ResourceConfig {

    public MagnusJerseyConfiguration() {
        scanPackagesAndRegisterResources("magnus.resources");
    }

    public void scanPackagesAndRegisterResources(String basePackage) {
        // 不同于spring-mvc会扫描@Controller注解来注册处理器列表（注册给dispatcherServlet），jersey需要自己注册相关的处理类。因此，使用Spring提供的class扫描工具来扫描并注册相关的处理类
        ClassPathScanningCandidateComponentProvider scanner =new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));
        this.registerClasses(scanner.findCandidateComponents(basePackage).stream()
                                    .map(beanDefinition -> ClassUtils.resolveClassName(
                                            Objects.requireNonNull(beanDefinition.getBeanClassName()),
                                            this.getClassLoader())).collect(Collectors.toSet()));
    }
}
