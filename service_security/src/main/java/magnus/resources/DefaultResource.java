package magnus.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/default")
public class DefaultResource {

    @GetMapping("/{id}")
    public String defaultResource(@PathVariable("id") String id) {
        return "this is the default resource " + id;
    }

    @GetMapping
    public String defaultResource() {
        return "this is the true default String";
    }
}
