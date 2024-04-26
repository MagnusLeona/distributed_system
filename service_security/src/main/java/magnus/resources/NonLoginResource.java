package magnus.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful")
public class NonLoginResource {

    @GetMapping
    public String noSecuredResource() {
        return "this is the non login resource";
    }
}
