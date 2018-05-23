package cn.lt.game.lib.widget.time;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.lt.game.R;
import cn.lt.game.lib.util.XmlParserHandler;
import cn.lt.game.model.CityModel;


public class WheelAreaMain {

	private View view;
	private WheelView wvProvince;
	private WheelView wvCitis;
	private WheelView wvDistrict;
	public int screenheight;
	
	private static class areaData{
		
		private static areaData areaData = new areaData();
		
		/**
		 * 所有省
		 */
		protected String[] mProvinceDatas;
		/**
		 * key - 省 value - 市
		 */
		protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
		/**
		 * key - 市 values - 区
		 */
		protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
		
//		/**
//		 * 当前省的名称
//		 */
//		protected String mCurrentProviceName;
//		/**
//		 * 当前市的名称
//		 */
//		protected String mCurrentCityName;
//		/**
//		 * 当前区的名称
//		 */
//		protected String mCurrentDistrictName ="";
		
		protected void initProvinceDatas(Context context){
			List<CityModel> provinceList = null;
	    	AssetManager asset = context.getAssets();
	        try {
	            InputStream input = asset.open("province_data.xml");
	            // 创建一个解析xml的工厂对象
				SAXParserFactory spf = SAXParserFactory.newInstance();
				// 解析xml
				SAXParser parser = spf.newSAXParser();
				XmlParserHandler handler = new XmlParserHandler();
				parser.parse(input, handler);
				input.close();
				// 获取解析出来的数据
				provinceList = handler.getDataList();
				//*/ 初始化默认选中的省、市、区
//				if (provinceList!= null && !provinceList.isEmpty()) {
//					mCurrentProviceName = provinceList.get(0).getName();
//					List<CityModel> cityList = provinceList.get(0).getCityList();
//					if (cityList!= null && !cityList.isEmpty()) {
//						mCurrentCityName = cityList.get(0).getName();
//						List<CityModel> districtList = cityList.get(0).getCityList();
//						mCurrentDistrictName = districtList.get(0).getName();
//						mCurrentZipCode = districtList.get(0).getZipcode();
//					}
//				}
				//*/
				mProvinceDatas = new String[provinceList.size()];
	        	for (int i=0; i< provinceList.size(); i++) {
	        		// 遍历所有省的数据
	        		mProvinceDatas[i] = provinceList.get(i).getName();
	        		List<CityModel> cityList = provinceList.get(i).getCityList();
	        		String[] cityNames = new String[cityList.size()];
	        		for (int j=0; j< cityList.size(); j++) {
	        			// 遍历省下面的所有市的数据
	        			cityNames[j] = cityList.get(j).getName();
	        			List<CityModel> districtList = cityList.get(j).getCityList();
	        			String[] distrinctNameArray = new String[districtList.size()];
	        			CityModel[] distrinctArray = new CityModel[districtList.size()];
	        			for (int k=0; k<districtList.size(); k++) {
	        				// 遍历市下面所有区/县的数据
	        				CityModel districtModel = new CityModel(districtList.get(k).getName());
	        				distrinctArray[k] = districtModel;
	        				distrinctNameArray[k] = districtModel.getName();
	        			}
	        			// 市-区/县的数据，保存到mDistrictDatasMap
	        			mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
	        		}
	        		// 省-市的数据，保存到mCitisDatasMap
	        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
	        	}
	        } catch (Throwable e) {  
	            e.printStackTrace();  
	        } finally {
	        	
	        } 
		}
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public WheelAreaMain(View view) {
		super();
		this.view = view;
		setView(view);
	}
	public WheelAreaMain(View view,boolean hasSelectTime) {
		super();
		this.view = view;
		setView(view);
	}
	/**
	 * @Description: 弹出地区选择器
	 */
	public void initDateAreaPicker() {
		areaData.areaData.initProvinceDatas(view.getContext());
		// 省
		wvProvince = (WheelView) view.findViewById(R.id.province);
		wvProvince.setAdapter(new ArrayWheelAdapter<String>(
				areaData.areaData.mProvinceDatas, 10));// 设置"省"的显示数据
//		wvProvince.setCyclic(true);// 可循环滚动
		wvProvince.setCurrentItem(0);// 初始化时显示的数据

		// 市
		wvCitis = (WheelView) view.findViewById(R.id.citis);
		wvCitis.setAdapter(new ArrayWheelAdapter<String>(
				areaData.areaData.mCitisDatasMap
						.get(areaData.areaData.mProvinceDatas[0]), 10));
//		wvCitis.setCyclic(true);
		wvCitis.setCurrentItem(0);

		// 县
		wvDistrict = (WheelView) view.findViewById(R.id.district);
//		wvDistrict.setCyclic(true);
		wvDistrict.setAdapter(new ArrayWheelAdapter<String>(
				areaData.areaData.mDistrictDatasMap
						.get(areaData.areaData.mCitisDatasMap
								.get(areaData.areaData.mProvinceDatas[0])[0]),
				10));
		wvDistrict.setCurrentItem(0);

		// 添加"省"监听
		OnWheelChangedListener wheelListenerProvince = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wvCitis.setAdapter(new ArrayWheelAdapter<String>(
						areaData.areaData.mCitisDatasMap
								.get(areaData.areaData.mProvinceDatas[newValue]),
						10));
				wvDistrict.setAdapter(new ArrayWheelAdapter<String>(
						areaData.areaData.mDistrictDatasMap
								.get(areaData.areaData.mCitisDatasMap
										.get(areaData.areaData.mProvinceDatas[newValue])[0]),
						10));
				wvCitis.setCurrentItem(0, true);
				wvDistrict.setCurrentItem(0, true);
			}
		};
		// 添加"市"监听
		OnWheelChangedListener wheelListenerCitis = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wvDistrict.setAdapter(new ArrayWheelAdapter<String>(
						areaData.areaData.mDistrictDatasMap
								.get(areaData.areaData.mCitisDatasMap
										.get(areaData.areaData.mProvinceDatas[wvProvince.getCurrentItem()])[newValue]),
						10));
				wvDistrict.setCurrentItem(0, true);
			}
		};
		wvProvince.addChangingListener(wheelListenerProvince);
		wvCitis.addChangingListener(wheelListenerCitis);

		// 指定选择器字体的大小
		int textSize = 0;
		textSize = view.getContext().getResources().getDimensionPixelOffset(R.dimen.font16sp);//(screenheight / 100) * 3;
		wvDistrict.TEXT_SIZE = textSize;
		wvCitis.TEXT_SIZE = textSize;
		wvProvince.TEXT_SIZE = textSize;

	}

	public String getArea() {
		String province = wvProvince.getAdapter().getItem(wvProvince.getCurrentItem());
		String district = wvCitis.getAdapter().getItem(wvCitis.getCurrentItem());
		String area = wvDistrict.getAdapter().getItem(wvDistrict.getCurrentItem());
		if(province.equals(district)){
			return district + area;
		}else{
			return province + district + area;
		}
	}

}
