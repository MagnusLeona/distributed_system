package magnus.leona.seata.aaa.resource;

import magnus.leona.seata.aaa.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @Autowired
    TransferService transferService;

    @RequestMapping("/transfer")
    public String transfer() {
        transferService.transfer();
        return "success";
    }
}
