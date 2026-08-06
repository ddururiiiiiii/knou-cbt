package knou.cbt.domain.siteoperation.service;

import knou.cbt.domain.siteoperation.dto.SiteOperationSettingRequest;
import knou.cbt.domain.siteoperation.mapper.SiteOperationSettingMapper;
import knou.cbt.domain.siteoperation.model.SiteOperationSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteOperationSettingServiceImpl implements SiteOperationSettingService {

    private final SiteOperationSettingMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public SiteOperationSetting get() {
        return mapper.find();
    }

    @Override
    public void update(SiteOperationSettingRequest request) {
        SiteOperationSetting setting = SiteOperationSetting.of(
                1L,
                request.isMaintenanceEnabled(),
                request.getMaintenanceMessage(),
                request.parseMaintenanceExpectedEndAt(),
                request.isBannerEnabled(),
                request.getBannerMessage(),
                request.parseBannerScheduledAt(),
                null
        );
        mapper.update(setting);
    }
}
