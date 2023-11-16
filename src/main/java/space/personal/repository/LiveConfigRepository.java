package space.personal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import space.personal.domain.LiveConfig;

public interface LiveConfigRepository extends CrudRepository<LiveConfig, Long> {
    @Query("SELECT lc FROM LiveConfig lc WHERE lc.custom_url =?1")
    LiveConfig findByCustomUrl(String customUrl);
    @Query("SELECT COUNT(lc) FROM LiveConfig lc WHERE lc.is_check = false")
    Long countByIsCheckFalse();

    @Modifying
    @Query("UPDATE LiveConfig lc SET lc.is_check = false")
    void setByIsCheckFalseAll();

    @Query("SELECT lc FROM LiveConfig lc WHERE lc.is_check = false")
    List<LiveConfig> findByIsCheckFalse();
}
