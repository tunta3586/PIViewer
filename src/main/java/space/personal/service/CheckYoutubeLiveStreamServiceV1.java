package space.personal.service;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import space.personal.domain.LiveConfig;
import space.personal.repository.LiveConfigRepository;

@Service
public class CheckYoutubeLiveStreamServiceV1 implements CheckYoutubeLiveStreamService {
    private LiveConfigRepository liveConfigRepository;
    private ChromeOptions chromeOptions;

    public CheckYoutubeLiveStreamServiceV1(LiveConfigRepository liveConfigRepository) {
        this.liveConfigRepository = liveConfigRepository;
        chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
    }

    @Override
    public void checkYoutubeLiveStream() throws InterruptedException {
        List<LiveConfig> liveConfigs = liveConfigRepository.findByIsCheckFalse();
        if(liveConfigs.size() > 0){
            WebDriver driver = new ChromeDriver(chromeOptions);
            for (LiveConfig liveConfig : liveConfigs) {
                if(!liveConfig.is_check()){
                    driver.get("https://www.youtube.com/"+ liveConfig.getCustom_url() + "/streams");
                    if(isLiveStream(driver)){
                        setLiveConfigInfo(driver, liveConfig);
                    }else{
                        resetLiveConfigInfo(liveConfig);
                    }
                    liveConfigRepository.save(liveConfig);
                }
            }
            driver.quit();
        }
    }

    @Override
    public void checkYoutubeLiveStreaminit() throws InterruptedException {
        Long liveConfigs = liveConfigRepository.countByIsCheckFalse();
        Long cnt = liveConfigRepository.count();
        if(liveConfigs == 0 && cnt != 0){
            liveConfigRepository.setByIsCheckFalseAll();
        }
    }

    private boolean isLiveStream(WebDriver driver) {
        return driver.getPageSource().contains("overlay-style=\"LIVE\"") &&
               driver.getPageSource().contains("<div class=\"yt-tab-shape-wiz__tab yt-tab-shape-wiz__tab--tab-selected\">라이브</div>");
    }

    private void setLiveConfigInfo(WebDriver driver, LiveConfig liveConfig) {
        liveConfig.setIs_live("Live");
        liveConfig.setVideo_id(driver.getPageSource().split("is-live-video")[1].split("content")[0].split("watch?")[1].substring(3, 14));
        liveConfig.set_check(true);
    }

    private void resetLiveConfigInfo(LiveConfig liveConfig) {
        liveConfig.setIs_live("");
        liveConfig.setVideo_id("");
        liveConfig.set_check(true);
    }
}