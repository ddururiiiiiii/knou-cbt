package knou.cbt.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthCheckMapper {
    String healthCheck();
}