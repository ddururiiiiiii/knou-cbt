package knou.cbt.domain.notice.mapper;

import knou.cbt.domain.notice.model.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<Notice> findAllPaged(@Param("offset") int offset, @Param("limit") int limit,
                              @Param("keyword") String keyword, @Param("useYn") String useYn);

    int countAll(@Param("keyword") String keyword,
                 @Param("useYn") String useYn);

    Notice findById(Long id);

    void insert(Notice notice);

    void update(Notice notice);

    void delete(Long id);

    List<Notice> findPinned();

    List<Notice> findPinnedAll();

    List<Notice> findRecentNormal(@Param("limit") int limit);
}