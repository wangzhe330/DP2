package Cinema;

import java.io.Serializable;

public class CinemaInfo implements Serializable{
	private String cinemaNameString;
	
	private String cinemaTelString;
	private String cinemaImgUrlString;
	
	private String business_id;
	private String regions;
	private String address;
	private String latitude;
	private String longitude;
	
	public void setName(String name) {
		this.cinemaNameString = name;
	}
	public void setLocation(String add){
		this.address = add;
	}
	public void setTel(String tel){
		this.cinemaTelString = tel;
	}
	public void setImgUrl(String url) {
		this.cinemaImgUrlString = url;
	}
	
	
	public String getName() {
		return this.cinemaNameString;
	}
	public String getLocation() {
		return this.address;
	}
	public String getTel() {
		return this.cinemaTelString;
	}
	public String getImgUrl() {
		return this.cinemaImgUrlString;
	}
	
	public void setBusinessId(String bi) {
		this.business_id = bi;
	}
	public String getBusinessId() {
		return this.business_id;
	}
	
	public void setLatitude(String lat) {
		this.latitude = lat;
	}
	public String getLatitude(){
		return this.latitude;
	}
	public void setLongitude(String lon){
		this.longitude =lon;
	}
	public String getLongitude(){
		return this.longitude;
	}
}
