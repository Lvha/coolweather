package com.example.lvhao.coolweather.db;

import org.litepal.crud.DataSupport;

public class province extends DataSupport {

    //省

    private int id;
    private String provinceName; //省名
    private String provinceCode;  //省的代码

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
