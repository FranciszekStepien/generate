package certyficate.datalogger.type;

import java.io.File;
import java.io.IOException;

import certyficate.datalogger.Logger;
import certyficate.datalogger.PointData;
import certyficate.datalogger.ReferenceFiles;

public class Rotronic extends Logger {
	protected static final String LINE_DATA_SEPARATOR = "\t";
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	protected static final String NUMBER_SEPARATOR = ",";
	protected static final String DATE_SEPARATOR = " ";
	
	private ReferenceFiles files;

	public Rotronic(boolean RH) {
		super(RH);
		files = new ReferenceFiles(this);
	}
	
	@Override
	protected void setData() {
		super.setData();
		nonDataLine = 47;
		deviceNumber = -1;
		findFile();
	}

	@Override
	protected PointData divide(String line) {
		PointData point = new PointData();
        String[] data = line.split(LINE_DATA_SEPARATOR);
        String date = setDate(data[0], data[1]);
        point.setDate(date, DATE_FORMAT);
        point.setTemperature(data[3], NUMBER_SEPARATOR);
        point.setHumidity(data[2], NUMBER_SEPARATOR);
        return point;
	}
	
	@Override
	protected void checkFileData() {
		if(checkCurrenPoint()) {
			checkNextPointData();
		}
	}

	@Override
	protected void setNewReader() throws IOException {
		currentPoint--;
		checkNextPointData();
	}
	
	private boolean checkCurrenPoint() {
		return currentPoint + 1 < calibrationPoints.size();
	}
	
	private void checkNextPointData() {
		checkNextPointDate();
		checkNextPoint();
	}

	private String setDate(String data, String time) {
		StringBuilder build = new StringBuilder(data);
		build.append(DATE_SEPARATOR);
		build.append(time);
		return build.toString();
	}
	
	private void findFile() {
		File file = files.findFile(currentPoint);
		if(file == null) {
			findNext();
		} else {
			newFile(file);
		}
	}

	private void newFile(File file) {
		try {
			setFile(file);
		} catch (IOException e) {
			System.out.println("file error");
		}
	}

	private void findNext() {
		currentPoint++;
		System.out.println(files.nonFile(currentPoint));
		findFile();
	}

	private void checkNextPointDate() {
		if(nextPointDate()) {
			data[currentPoint + 1] = data[currentPoint];
			currentPoint++;
		}
	}

	private boolean nextPointDate() {
		return calibrationPoints.get(currentPoint).
				equalDate(calibrationPoints.get(currentPoint + 1));
	}
	
	private void checkNextPoint() {
		if(checkCurrenPoint() && !nextPointDay()) {
			currentPoint++;
			findFile();
			findPointFromNewFile();
		}
	}
	
	private void findPointFromNewFile() {
		try {
			findPoint();
		} catch (IOException e) {
			System.out.println("file error");
		}
	}

	private boolean nextPointDay() {
		return calibrationPoints.get(currentPoint).
				equalDay(calibrationPoints.get(currentPoint + 1));
	}
}