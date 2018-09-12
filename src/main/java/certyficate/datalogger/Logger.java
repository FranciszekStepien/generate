package certyficate.datalogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

import certyficate.files.ReaderCreator;
import certyficate.sheetHandlers.CalibrationPoint;

public abstract class Logger {
	//TODO reorganizing messages
	private static final String NON_DATA_INFROMATION = "No data for point ";
	private static final String GET_DATA = "pobieranie danych dla punktu: ";
	private static final String TIME = " z godziny: ";
	private static final String END_OF_FILE = "End of File"; 
	
	private static final int MEASUREMENTS_POINTS = 10;
	
	private String serialNumber;
	
	private File file;
	
	private BufferedReader reader;
	
	protected boolean Rh;
	
	protected int nonDataLine;
	
	protected List<CalibrationPoint> calibrationPoints;
	
	protected PointData[][] data;
	
	protected int currentPoint;
	protected int deviceNumber;
	
	public Logger(boolean RH){
		this.Rh = RH;
	}
	
	public void setFile(File file) throws IOException {
		this.file= file;
		setReader();
	}
	
	public PointData[][] getPointData() {
		return data;
	}
	
	public int getDeviceNumber() {
		return deviceNumber;
	}
	
	public List<CalibrationPoint> getCalibrationPoints() {
		return calibrationPoints;
	}

	public void setDeviceNumber(int index) {
		deviceNumber = index;
	}
	
	public void getData(List<CalibrationPoint> calibrationPoints) {
		this.calibrationPoints = calibrationPoints;
		setData();
		findPointsData();
		closeReader();
	}
	
	public void setSerialNumber(String deviceName) {
		serialNumber = deviceName;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	
	protected void findPoint() throws IOException {
		String line = reader.readLine();
		while(checkLine(line)) {
			checkPoint(line);
			line = reader.readLine();
		}
	}

	protected void setData() {
		data = new PointData[calibrationPoints.size()][MEASUREMENTS_POINTS];
		currentPoint = 0;
	}
	
	protected void checkFileData() {}
	
	protected abstract PointData divide(String nextLine);
	
	protected void setNewReader() throws IOException {
		setReader();
	}
	
	private void setReader() throws IOException {
    	reader = ReaderCreator.getReader(file);
    	removeNonDataLine();
    }

	private void removeNonDataLine() throws IOException {
		for(int i = 0; i < nonDataLine; i++) {
			reader.readLine();
		}
	}

	private void findPointsData() {
		try {
			findPoint();
		} catch (IOException e) {
			noDataMessage();
		}
	}
	
	private void noDataMessage() {
		System.out.println(noDataInformation());
		setNonDataInCurrentPoint();
	}
	
	private void setNonDataInCurrentPoint() {
		data[currentPoint] = null;
		calibrationPoints.get(currentPoint).nonData();
	}

	private StringBuilder noDataInformation() {
		StringBuilder build = new StringBuilder(NON_DATA_INFROMATION);
		build.append(currentPoint + 1);
		return build;
	} 

	private boolean checkLine(String line) throws IOException {
		boolean isData = checkPoint();
		if(isData) {
			isData = isLineData(line);
		}
		return isData;
	}
	
	private boolean isLineData(String line) throws IOException {
		boolean answer = true;
		if(line == null) {
			answer = false;
			nextPoint();
		}
		return answer;
	}

	private void nextPoint() throws IOException {
		noDataMessage(); 
		checkNewReader();
	}
	
	private void checkNewReader() throws IOException {
		currentPoint++;
		if(checkPoint()) {
			setNewReader();
		}
	}

	private boolean checkPoint() {
		return currentPoint < calibrationPoints.size();
	}
	
	private void checkPoint(String line) throws IOException {
		CalibrationPoint point = calibrationPoints.get(currentPoint);
		PointData pointData = divide(line);
		if(pointData.equalsData(point)) {
			setPointsData(pointData);
		}
	}

	private void setPointsData(PointData pointData) throws IOException {
		insertPointDataMessage(pointData.getTime());
		data[currentPoint][0] = pointData;
		setPointData();
		checkNextPoint();
	}

	private void checkNextPoint() {
		currentPoint++;
		if(checkPoint()) {
			checkTimeAndData();
		}
	}

	private void checkTimeAndData() {
		CalibrationPoint point = calibrationPoints.get(currentPoint - 1);
		CalibrationPoint nextPoint = calibrationPoints.get(currentPoint);
		if(point.equalDate(nextPoint)) {
			data[currentPoint] = null;
			checkNextPoint();
		}
	}

	private void insertPointDataMessage(String time) {
		StringBuilder build = new StringBuilder(GET_DATA);
		build.append(currentPoint + 1);
		build.append(TIME);
		build.append(time);
		System.out.println(build);
	}

	private void setPointData() throws IOException {
		for(int i = 1; i < MEASUREMENTS_POINTS; i++) {
			setLine(i);
		}
		checkFileData();
	}

	private void setLine(int index) throws IOException {
		if(checkPoint()) {
			data[currentPoint][index] = setLine();
		} 
	}

	private PointData setLine() throws IOException {
		String line = reader.readLine();
		checkLineData(line);
		return divide(line);
	}
	
	private void checkLineData(String line) throws IOException {
		if(line == null) {
			System.out.println(END_OF_FILE);
			throw new IOException();
		}
	}
	
	private void closeReader() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}