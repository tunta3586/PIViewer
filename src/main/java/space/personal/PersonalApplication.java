package space.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class PersonalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalApplication.class, args);
	}

	@PostConstruct
	public void init() {
        // Chrome 웹 드라이버 경로를 설정
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

}
