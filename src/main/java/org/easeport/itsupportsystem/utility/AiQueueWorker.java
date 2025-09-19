package org.easeport.itsupportsystem.utility;


import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.easeport.itsupportsystem.service.EmailProcessingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
public class AiQueueWorker {

    private final BlockingQueue<RawEmail> emailQueue;
    private final EmailProcessingService emailProcessingService;


    public AiQueueWorker(BlockingQueue<RawEmail> emailQueue, EmailProcessingService emailProcessingService) {
        this.emailQueue = emailQueue;
        this.emailProcessingService = emailProcessingService;
    }

    @Scheduled(fixedDelay = 20000)
    public void processNextEmail() {

        try {
            RawEmail email = emailQueue.take();
            System.out.println(email.getContent());
            emailProcessingService.processRawMailThroughAi(email);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
