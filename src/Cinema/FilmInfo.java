package Cinema;

import java.io.Serializable;

public class FilmInfo implements Serializable{
	private CinemaInfo cinameInfo;
	public String detailInfo;
	
	public FilmInfo(CinemaInfo cinemaInfo, String filmInfo)
	{
		this.cinameInfo = cinemaInfo;
		this.detailInfo = filmInfo;
	}
	
	public CinemaInfo getCinemaInfo()
	{
		return this.cinameInfo;
	}
	
	public String getDetailInfo()
	{
		return detailInfo;
	}
}
