package space.personal.service;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import space.personal.domain.LiveConfig;
import space.personal.mappers.LiveConfigMapper;

public class CheckYoutubeLiveStreamServiceV2 implements CheckYoutubeLiveStreamService {

    private LiveConfigMapper liveConfigMapper;
    private ChromeOptions chromeOptions;

    public CheckYoutubeLiveStreamServiceV2(LiveConfigMapper liveConfigMapper) {
        this.liveConfigMapper = liveConfigMapper;
        chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
    }

    @Override
    public void checkYoutubeLiveStream() throws InterruptedException {
        int checkCount = liveConfigMapper.countByIsCheckFalse();
        if(checkCount > 0){
            List<LiveConfig> liveConfigs = liveConfigMapper.findByIsCheckFalse();
            WebDriver driver = new ChromeDriver(chromeOptions);
            for (LiveConfig liveConfig : liveConfigs) {
                if(!liveConfig.is_check()){
                    driver.get("https://www.youtube.com/"+ liveConfig.getCustom_url() + "/streams");
                    if(isLiveStream(driver)){
                        liveConfig = setLiveConfigInfo(driver, liveConfig);
                    }else{
                        liveConfig = resetLiveConfigInfo(liveConfig);
                    }
                    liveConfigMapper.updateLiveConfig(liveConfig);
                }
            }
            driver.quit();
        }
    }

    @Override
    public void checkYoutubeLiveStreaminit() throws InterruptedException {
        int checkCount = liveConfigMapper.countByIsCheckFalse();
        if(checkCount == 0){
            liveConfigMapper.setByIsCheckFalseAll();
        }
    }

    private boolean isLiveStream(WebDriver driver) {
        return driver.getPageSource().contains("overlay-style=\"LIVE\"") &&
               driver.getPageSource().contains("<div class=\"yt-tab-shape-wiz__tab yt-tab-shape-wiz__tab--tab-selected\">라이브</div>");
    }

    private LiveConfig setLiveConfigInfo(WebDriver driver, LiveConfig liveConfig) {
        liveConfig.setIs_live("Live");
        liveConfig.setVideo_id(driver.getPageSource().split("is-live-video")[1].split("content")[0].split("watch?")[1].substring(3, 14));
        liveConfig.set_check(true);
        return liveConfig;
    }

    private LiveConfig resetLiveConfigInfo(LiveConfig liveConfig) {
        liveConfig.setIs_live("");
        liveConfig.setVideo_id("");
        liveConfig.set_check(true);
        return liveConfig;
    }
}
