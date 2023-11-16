package space.personal.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import space.personal.domain.LiveConfig;

@Mapper
public interface LiveConfigMapper {
    void save(LiveConfig liveConfig);
    void updateLiveConfig(LiveConfig liveConfig);
    void setByIsCheckFalseAll();
    void delete(LiveConfig liveConfig);
    LiveConfig findByCustomUrl(String custom_url);
    int existsByCustomUrl(String custom_url);
    int countByIsCheckFalse();
    List<LiveConfig> findAll();
    List<LiveConfig> findByIsCheckFalse();
}