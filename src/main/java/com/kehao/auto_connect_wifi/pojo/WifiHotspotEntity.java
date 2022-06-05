package com.kehao.auto_connect_wifi.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Builder
@Data
public class WifiHotspotEntity {
    private String name;
    private int signal;//信号强度

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WifiHotspotEntity that = (WifiHotspotEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
