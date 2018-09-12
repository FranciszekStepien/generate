package certyficate.sheetHandlers.search.measurments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import certyficate.entitys.Order;
import certyficate.property.CalibrationData;
import certyficate.property.SheetData;
import certyficate.sheetHandlers.CalibrationPoint;

public class MeasurementsData {
	private static final String EMPTY_CELL = "";
	
	private static List<CalibrationPoint> points;
	
	private static List<Order> toRemove;
    
	private static List<Measurements> devices;
	
	private static Sheet sheet;

    public static void findProbeData() {
    	int line = SheetData.startRow - CalibrationData.numberOfParameters;
    	Measurements reference = MeasurementResults.findDeviceResults(line);
    	CalibrationData.patern = reference;
	}
	
	public static void findMeasurementsData() throws IOException {
		setSheet();
		setRemoveList();
		getCalibtationPoints();
		findMeasurments();
	}
	
	private static void findMeasurments() {
		devices = MeasurementResults.findMeasurmentData(sheet);
		addMeasurmentsToOrders();
		removeOrderWithoutMeasurments();
	}

	private static void setSheet() throws IOException {
		File file = CalibrationData.sheet;
		sheet = SpreadSheet.createFromFile(file).getSheet(SheetData.sheetName);
		toRemove = new ArrayList<Order>();
	}
	
	private static void setRemoveList() {
		toRemove = new ArrayList<Order>();
	}
	
	private static void getCalibtationPoints() {
		points = new ArrayList<CalibrationPoint>();
		setPoints();
		CalibrationData.point = points;
	}

	private static void setPoints() {
		int line = 6;
		for(int i = 0; i < CalibrationData.calibrationPoints; i++) {
			addCalibrationPoint(line);
			line += SheetData.pointGap;
		}
	}

	private static void addCalibrationPoint(int line) {
		CalibrationPoint point = new CalibrationPoint();
		point.setDate(sheet.getValueAt(SheetData.dateColumn, line)
				.toString());
		setTime(point, line);
		point.setPointNumber(points.size());
		point.setPointData(getPoints(line));
		points.add(point);
	}

	private static void setTime(CalibrationPoint point, int line) {
		String time = sheet.getValueAt(SheetData.timeColumn, line).toString();
		if(!EMPTY_CELL.equals(time)) {
			point.setTime(time);
		}
	}

	private static double[] getPoints(int line) {
		double[] point = new double[CalibrationData.numberOfParameters];
		for(int i = 0; i < CalibrationData.numberOfParameters; i++) {
			point[i] = getPoint(line, i);
		}
		return point;
	}
	
	private static double getPoint(int line, int index) {
		int column = 2 - CalibrationData.numberOfParameters + index;
		return Double.parseDouble(sheet.getValueAt(column, line).toString());
	}

	private static void addMeasurmentsToOrders() {
		for(Order order: CalibrationData.orders) {
			findAndAddMeasurmentsData(order);
			checkMeasurmentsData(order);
		}
	}

	private static void findAndAddMeasurmentsData(Order order) {
		for(Measurements device: devices) {
			check(device, order);
		}
	}

	private static void check(Measurements device, Order order) {
		if(device.match(order)) {
			order.setMeasurmet(device.measurmets);
		}
	}
	
	private static void checkMeasurmentsData(Order order) {
		if(order.getMeasurmets() == null) {
			toRemove.add(order);
		}
	}
	
	private static void removeOrderWithoutMeasurments() {
		for(Order order: toRemove) {
			CalibrationData.orders.remove(order);
		}
		toRemove = null;
	}
}
 

