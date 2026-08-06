package knou.cbt.domain.siteoperation.mapper;

import knou.cbt.domain.siteoperation.model.SiteOperationSetting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteOperationSettingMapper {

    SiteOperationSetting find();

    void update(SiteOperationSetting setting);
}
