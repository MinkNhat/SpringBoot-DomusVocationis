package vn.nmn.domusvocationis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nmn.domusvocationis.service.EmailService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
//    @Scheduled(cron = "*/60 * * * * *")
//    @Transactional
    public String sendSimpleEmail() {
//        this.subscriberService.sendSubscribersEmailJobs();

        return "ok";
    }
}
