package knou.cbt.domain.siteoperation.service;

import knou.cbt.domain.siteoperation.dto.SiteOperationSettingRequest;
import knou.cbt.domain.siteoperation.model.SiteOperationSetting;

public interface SiteOperationSettingService {

    SiteOperationSetting get();

    void update(SiteOperationSettingRequest request);
}
