package certyficate.generate.note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import certyficate.entitys.Order;
import certyficate.entitys.Probe;
import certyficate.equipment.calculation.DataProbe;
import certyficate.entitys.Device;
import certyficate.files.PathCreator;
import certyficate.generate.CertificateText;
import certyficate.generate.CertificateValue;
import certyficate.generate.Special;
import certyficate.generate.certificate.Certificate;
import certyficate.property.CalibrationData;
import certyficate.property.DataCalculation;

//TODO remove T/Rh poll, make class smaller again
public abstract class Note {
	private static final String NOTE_SHEET = "Wyniki wzorcowania";
	private static final String DEFAULT_NUMBER_SEPRATOR = "\\.";
	private static final String CUSTOM_NUMBER_SEPRATOR = ",";
	private static final String FILE_TEXT_SEPARATOR = "_";
	private static final String FILE_EXTENSION = ".ods";
	
	private static final int POINT_GAP = 32;
	private static final int PROBE_COLUMN = 4;
	protected static final int DEVICE_COLUMN = 3;
	
	protected String noteFile;
	
	protected static Sheet sheet;
	
	private static List<CertificateValue> calibrationData;
	
	protected static int calibrationPointCount;
	protected int numberOfData;
	private static int channelCount;
	
	protected static double round;
	
	protected static Order order;
	
	protected static Certificate certificate;
	
	protected static DataProbe[] reference;
	
	private static String[] environment;
	
	public Note() {
		setCalibrationData();
		setCertificate();
		setData();
	}

	public void setNoteAndCertificate(Order orderData) throws IOException {
		setCalibrationData(orderData);
		setNoteData();
		checkDocumentData();
	}

	public boolean isCalibrationData() {
		return !calibrationData.isEmpty();
	}
	
	protected abstract void setCertificate();
	
	protected abstract void setData();
	
	protected boolean havePointData(int index) {
		boolean haveData = order.getMeasurments(index).haveMeasurments;
		haveData &= reference[index].haveData();
		haveData &= reference[index].getValue(0) 
				== order.getPoint(calibrationPointCount, 0);
		return haveData;
	}
	
	protected abstract void setResolution(Device device, int line);
	
	protected abstract void setValue(int line, int index, int i);

	protected void setCalibrationBudget(int line, int index) {
		sheet.setValueAt(order.getMeasurments(index).average[0], 7 , line + 5);
        sheet.setValueAt(order.getDevice().getResolution(0), 9 , line + 6);
        setReference(reference[index], line);
	}
	
	protected void setProbeSerial(String[] probeSerial, int line) {
		int probeNumber = calibrationPointCount / numberOfData;
		if(probeNumber < probeSerial.length) {
			sheet.setValueAt(probeSerial[probeNumber], PROBE_COLUMN , line);
		}
	}
	
	protected double findUncerinityAndRound(double[] uncerinities, int index) {
		double uncerinity = DataCalculation.uncertainty(uncerinities);
		round = DataCalculation.findRound(2 * uncerinity, 
				Double.parseDouble(order.getDevice().getResolution(index)));
		return uncerinity;
	}
	
	protected String setNumber(double number) {
		String rounded = DataCalculation.round(number, round);
		return rounded.replaceAll(DEFAULT_NUMBER_SEPRATOR, CUSTOM_NUMBER_SEPRATOR);
	}

	protected void setPointValue(int line, CertificateValue pointValue) {
		sheet.setValueAt(pointValue.getProbe(0), 5, line + 17);
		sheet.setValueAt(pointValue.getDevice(0), 7, line + 17);
		sheet.setValueAt(pointValue.getError(0), 9, line + 17);
		sheet.setValueAt(pointValue.getUncertainty(0), 13, line + 17);
		addPointValue(pointValue);
	}
	
	protected void addPointValue(CertificateValue pointValue) {
		calibrationData.add(pointValue);		
	}
	
	protected abstract CertificateValue findPointValue(int line, int index);
	
	private void setCalibrationData() {
		environment = CertificateText.getEnviromentData();
		setEquipmentData();
	}
	
	private static void setEquipmentData() {
		reference = CalibrationData.probe;		
	}

	private static void setCalibrationData(Order orderData) {
		order = orderData;
		channelCount = 0;
	}

	private void setNoteData() throws FileNotFoundException, IOException {
		checkOrder();
		setSheet();
		setSheetData();
	}
	
	private void checkOrder() {
		Special.checkSpecial(order);
	}

	private void setSheet() throws IOException {
		String pathToNote = PathCreator.filePath(noteFile);
		File noteFile = new File(pathToNote);
		sheet = SpreadSheet.createFromFile(noteFile).getSheet(NOTE_SHEET);
	}
	
	private void setSheetData() {
		calibrationData = new ArrayList<CertificateValue>();
		calibrationPointCount = 0;
		for(int i = 0; i < CalibrationData.calibrationPoints; i++) { 
			checkData(i);
		}
	}

	private void checkData(int index) {
		if(calibrationPointCount < order.getPointLength()) {
			checkPointData(index);
		}
	}
	
	private void checkPointData(int index) {
		if(havePointData(index)) {
        	setPointData(index);
        }
	}

	private void setPointData(int index) {
		int line = getLine();
		CertificateValue pointValue = findPointValue(line, index);
		setConstantValue(line);
		setMeasurmentValue(line + 17, index);
		setCalibrationBudget(line, index);
		setPointValue(line, pointValue);
		calibrationPointCount++;
		checkCalibrationPointCount();
	}

	private int getLine() {
		int answer = calibrationPointCount + channelCount * order.getChannelNumber();
		return answer * POINT_GAP + 3;
	}

	private void checkCalibrationPointCount() {
		if(calibrationPointCount == order.getPointLength()) {
			channelCount++;
			chceckChannelCount();
		}
	}

	private void chceckChannelCount() {
		if(channelCount < order.getChannelNumber()) {
			calibrationPointCount = 0;
		}
	}

	private void setConstantValue(int line) {
        setCertificate(line);
        setEnvironment(line + 1);
        setDevice(order.getDevice(), line);
        if(order.getProbe() != null) {
        	setProbeData(line);
        }
	}

	private void setCertificate(int line) {
		sheet.setValueAt(order.getNumberOfCalibration(), 3, line);
        sheet.setValueAt(order.getCalibrationCode(), 8, line);
        sheet.setValueAt(order.getCalibrationDate(), 13, line);
        sheet.setValueAt(order.getDeviceSerialNumber(), 3, line + 9);
	}

	private void setEnvironment(int environmentLine) {
		sheet.setValueAt(environment[0], 3, environmentLine);
        sheet.setValueAt(environment[1], 7, environmentLine);
	}

	private void setDevice(Device device, int line) {
        sheet.setValueAt(device.getType(), DEVICE_COLUMN, line + 3);
        sheet.setValueAt(device.getModel(), DEVICE_COLUMN, line + 6);
        sheet.setValueAt(device.getProducent(), DEVICE_COLUMN, line + 11);
        setChannel(device, line + 8);
        setResolution(device, line + 13);
	}

	private void setChannel(Device device, int line) {
		int channelNumber = calibrationPointCount / numberOfData;
		if(channelNumber < device.getChannelLength()) {
			sheet.setValueAt(device.getChannel(channelNumber), DEVICE_COLUMN , line);
		}
	}

	private void setProbeData(int line) {
		setProbe(order.getProbe() ,line);
    	setProbeSerial(order.getProbeSerial(), line + 9);
	}
	
	private static void setProbe(Probe probe, int line) {
		sheet.setValueAt(probe.getModel(), PROBE_COLUMN, line + 6);
		sheet.setValueAt(probe.getProducent(), PROBE_COLUMN, line + 11);
	}
	
	private void setMeasurmentValue(int line, int index) {
		for(int i = 0; i < CalibrationData.MEASUREMENTS_POINTS; i++) {
			setValue(line + i, index, i);
		}
	}
	
	private void setReference(DataProbe reference, int line) {
		sheet.setValueAt(reference.getCorrection(0), 7 , line + 9);
		sheet.setValueAt(reference.getUncertainty(0), 9, line + 9);
        sheet.setValueAt(reference.getDrift(0), 9, line + 10);
	}
	
	private void checkDocumentData() throws IOException {
		if(isCalibrationData()) {
			createDocuments();
		}
	}	
	
	private void createDocuments() throws IOException {
		safeFile();
		certificate.setCertificate(order, calibrationData);
	}	

	private static void safeFile() throws FileNotFoundException, IOException {
		File noteFile = noteFile();
		sheet.getSpreadSheet().saveAs(noteFile);
	}

	private static File noteFile() {
		File notes = CalibrationData.notes;
		String fileName = setFileName();
		File note = new File(notes, fileName);
		return note;
	}

	private static String setFileName() {
		StringBuilder bulid = new StringBuilder(order.getNumberOfCalibration());
		bulid.append(FILE_TEXT_SEPARATOR);
		bulid.append(order.getDevice().getModel());
		bulid.append(FILE_EXTENSION);
		return bulid.toString();
	}
}