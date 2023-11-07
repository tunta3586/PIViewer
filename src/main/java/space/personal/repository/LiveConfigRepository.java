package space.personal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import space.personal.domain.LiveConfig;

public interface LiveConfigRepository extends CrudRepository<LiveConfig, Long> {
    LiveConfig findByCustomUrl(String customUrl);
    long countByIsCheckFalse();

    @Modifying
    @Query("UPDATE LiveConfig lc SET lc.isCheck = false")
    void setByIsCheckFalseAll();

    @Query("SELECT lc FROM LiveConfig lc WHERE lc.isCheck = false")
    List<LiveConfig> findByIsCheckFalse();
}
