package certyficate.sheetHandlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import certyficate.property.CalibrationData;

public class CalibrationPoint{
	private static final String SHEET_DATE_FORMAT = "EEE MMM dd hh:mm:ss z yyyy";
	private static final String DATE_FORMAT = "dd.MM.yyyy";
	private static final String SHEET_TIME_FORMAT = "'PT'HH'H'mm'M's'S'";
	private static final String TIME_FORMAT = "HH:mm";
	private static final String PATTERN_FORMAT = "yyyyMMdd";
	
	private static final Locale LOCALE =  Locale.US;
	
	private double[] point;
	
	private int number;
	
	private boolean isData = true;
	
	private String date;
	private String time;
	
	private Date pointDate;
	
	public CalibrationPoint() {
		point = new double[CalibrationData.numberOfParameters];
	}
	
	public Date getPointDate() {
		return pointDate;
	}
	
	public double[] getPointData() {
		return point;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getPatternDate() {
		return new SimpleDateFormat(PATTERN_FORMAT).format(pointDate);
	}
	
	public String getTime() {
		return time;
	}
	
	public int getPointNumber() {
		return number;
	}
	
	public void nonData() {
		isData = false;
	}
	
	public void setPointNumber(int number){
		this.number= number;
	}	
	
	public void setPointData(double[] point) {
		this.point = point;
	}
	
	public boolean equalDate(CalibrationPoint point) {
		return pointDate.equals(point.getPointDate());
	}
	
	public boolean equalDay(CalibrationPoint point) {
		return isData && date.equals(point.getDate());
	}

	public void setDate(String dateString) {
		pointDate = getDate(dateString);
		date = new SimpleDateFormat(DATE_FORMAT).format(pointDate);
	}

	private Date getDate(String dateString) {
		Date date;
    	try {
			date = new SimpleDateFormat(SHEET_DATE_FORMAT, LOCALE).parse(dateString);
		} catch (ParseException e) {
			date = defaultDateFormat(dateString);
		}
		return date;
	}

	private Date defaultDateFormat(String dateString) {
		Date date;
		try {
			date = new SimpleDateFormat(DATE_FORMAT, LOCALE).parse(dateString);
		} catch (ParseException e) {
			date = null;
			e.printStackTrace();
		}
		return date;
	}

	public void setTime(String timeString) {
		Date dataTime = getTime(timeString);
		setPointTime(dataTime);	
	}

	private Date getTime(String timeString) {
		Date date;
		try {
			date = new SimpleDateFormat(SHEET_TIME_FORMAT).parse(timeString);
		} catch (ParseException e) {
			date = defaultTimeFormat(timeString);
		}
		return date;
	}
	
	private Date defaultTimeFormat(String timeString) {
		Date date;
		try {
			date = new SimpleDateFormat(TIME_FORMAT, LOCALE).parse(timeString);
		} catch (ParseException e) {
			date = null;
			e.printStackTrace();
		}
		return date;
	}
	
	@SuppressWarnings("deprecation")
	private void setPointTime(Date dataTime) {
		time = new SimpleDateFormat(TIME_FORMAT).format(dataTime);
		pointDate.setHours(dataTime.getHours());
		pointDate.setMinutes(dataTime.getMinutes());
	}
    

	public static Comparator<CalibrationPoint> comparator = new Comparator<CalibrationPoint>() {
		public int compare(CalibrationPoint point1, CalibrationPoint point2) {
			return point1.pointDate.compareTo(point2.pointDate);
		}
	};
	
}