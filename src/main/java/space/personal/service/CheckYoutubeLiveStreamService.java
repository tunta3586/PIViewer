package space.personal.service;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import space.personal.domain.LiveConfig;
import space.personal.repository.LiveConfigRepository;


@Service
public class CheckYoutubeLiveStreamService {
    private LiveConfigRepository liveConfigRepository;
    private ChromeOptions chromeOptions;

    public CheckYoutubeLiveStreamService(LiveConfigRepository liveConfigRepository) {
        this.liveConfigRepository = liveConfigRepository;
        chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
    }

    @Async("checkYoutubeLiveStream")
    @Scheduled(fixedRate = 5000)
    public void checkYoutubeLiveStream() throws InterruptedException {
        List<LiveConfig> liveConfigs = (List<LiveConfig>) Optional.ofNullable(liveConfigRepository.findByIsCheckFalse()).orElse(null);
        if(liveConfigs.size() > 0){
            WebDriver driver = new ChromeDriver(chromeOptions);
            for (LiveConfig liveConfig : liveConfigs) {
                if(!liveConfig.isCheck()){
                    driver.get("https://www.youtube.com/"+ liveConfig.getCustomUrl() + "/streams");
                    if(driver.getPageSource().contains("overlay-style=\"LIVE\"") 
                    && driver.getPageSource().contains("<div class=\"yt-tab-shape-wiz__tab yt-tab-shape-wiz__tab--tab-selected\">라이브</div>")){
                        liveConfig.setIsLive("Live");
                        liveConfig.setVideoId(driver.getPageSource().split("is-live-video")[1].split("content")[0].split("watch?")[1].substring(3,14));
                        liveConfig.setCheck(true);
                    }else{
                        liveConfig.setIsLive("");
                        liveConfig.setVideoId("");
                        liveConfig.setCheck(true);
                    }
                    liveConfigRepository.save(liveConfig);
                }
            }
            driver.quit();
        }
    }

    @Async("checkYoutubeLiveStreamInit")
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkYoutubeLiveStreaminit() throws InterruptedException {
        Long liveConfigs = liveConfigRepository.countByIsCheckFalse();
        Long cnt = liveConfigRepository.count();
        if(liveConfigs == 0 && cnt != 0){
            liveConfigRepository.setByIsCheckFalseAll();
        }
    }
}