package space.personal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class LiveConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
    @Column(name = "custom_url")
    private String customUrl;
    @Column(name = "thumbnails_url")
    private String thumbnailsUrl;

    // 나중에 Enum으로 다시 처리할 예정
    @Column(name = "is_live")
    private String isLive;
    @Column(name = "video_id")
    private String videoId;
    @Column(name = "is_check")
    private boolean isCheck;
}
