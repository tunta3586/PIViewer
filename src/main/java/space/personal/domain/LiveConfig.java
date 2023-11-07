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
    private String customUrl;
    private String thumbnailsUrl;

    // 나중에 Enum으로 다시 처리할 예정
    private String isLive;
    private String videoId;
    private boolean isCheck;
}
