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

@Configuration
@ApplicationPath("/restful")
public class MagnusJerseyConfiguration extends ResourceConfig {

    public void scanPackagesAndRegisterResources(String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));
        this.registerClasses(scanner.findCandidateComponents(basePackage).stream()
                                    .map(beanDefinition -> ClassUtils.resolveClassName(
                                            Objects.requireNonNull(beanDefinition.getBeanClassName()),
                                            this.getClassLoader())).collect(Collectors.toSet()));
    }
}
