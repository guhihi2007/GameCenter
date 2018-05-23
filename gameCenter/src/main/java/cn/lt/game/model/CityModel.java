package cn.lt.game.model;

import java.util.List;

public class CityModel {
	private String name;
	private List<CityModel> cityList;
	
	public CityModel() {
		super();
	}

	public CityModel(String name, List<CityModel> districtList) {
		super();
		this.name = name;
		this.cityList = districtList;
	}
	
	public CityModel(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CityModel> getCityList() {
		return cityList;
	}

	public void setCityList(List<CityModel> cityList) {
		this.cityList = cityList;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
