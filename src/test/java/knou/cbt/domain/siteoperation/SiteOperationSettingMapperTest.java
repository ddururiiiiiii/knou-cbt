package knou.cbt.domain.siteoperation;

import knou.cbt.domain.siteoperation.mapper.SiteOperationSettingMapper;
import knou.cbt.domain.siteoperation.model.SiteOperationSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class SiteOperationSettingMapperTest {

    @Autowired
    private SiteOperationSettingMapper mapper;

    @Test
    void updateAndFindRoundTrip() {
        SiteOperationSetting setting = SiteOperationSetting.of(
                1L, true, "테스트 메시지", null, false, "", null, null
        );

        mapper.update(setting);

        SiteOperationSetting found = mapper.find();
        assertThat(found.getMaintenanceEnabled()).isTrue();
        assertThat(found.getMaintenanceMessage()).isEqualTo("테스트 메시지");

        mapper.update(SiteOperationSetting.of(1L, false, null, null, false, null, null, null));
    }
}
