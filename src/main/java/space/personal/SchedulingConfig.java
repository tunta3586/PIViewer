package space.personal;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import space.personal.service.CheckYoutubeLiveStreamService;

@Configuration
public class SchedulingConfig {
    private final CheckYoutubeLiveStreamService checkYoutubeLiveStreamService;
    
    public SchedulingConfig(CheckYoutubeLiveStreamService checkYoutubeLiveStreamService){
        this.checkYoutubeLiveStreamService = checkYoutubeLiveStreamService;
    }

    @Async("checkYoutubeLiveStream")
    @Scheduled(fixedRate = 5000) 
    public void checkYoutubeLiveStream() throws InterruptedException{
        checkYoutubeLiveStreamService.checkYoutubeLiveStream();
    }

    @Async("checkYoutubeLiveStreamInit")
    @Scheduled(fixedRate = 60000)
    public void checkYoutubeLiveStreaminit() throws InterruptedException{
        checkYoutubeLiveStreamService.checkYoutubeLiveStreaminit();
    }
}
