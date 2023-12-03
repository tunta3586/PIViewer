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
    private String custom_url;
    @Column(name = "thumbnails_url")
    private String thumbnails_url;

    @Column(name = "is_live")
    private String is_live;
    @Column(name = "video_id")
    private String video_id;
    @Column(name = "is_check")
    private boolean is_check;
}
