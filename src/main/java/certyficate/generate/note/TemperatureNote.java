package certyficate.generate.note;


import certyficate.equipment.calculation.DataProbe;
import certyficate.generate.CertificateValue;
import certyficate.generate.DataCalculation;
import certyficate.generate.certificate.TemperatureCertificate;
import certyficate.property.CalibrationData;
import certyficate.sheetHandlers.search.Measurements;

public class TemperatureNote extends Note {
	protected String noteFile = "z_T.ods";
	
	protected Measurements referenceValue;
	
	protected DataProbe[] chamber;
	
	public TemperatureNote() {
		super();
		setDeviceData();
	}

	private void setDeviceData() {
		referenceValue = CalibrationData.patern;
		chamber = CalibrationData.chamber;
	}

	@Override
	protected void setResolution(String[] resolution, int line) {
		sheet.setValueAt(resolution[0], 3, line);
	}


	@Override
	protected void setValue(int line, int index, int point) {
		String time = CalibrationData.point.get(index).getTime();
		sheet.setValueAt(DataCalculation.time(time, point), 0, line);
		sheet.setValueAt(referenceValue.measurmets[index].data[point][0], 
				1, line);
		sheet.setValueAt(order.measurmets[index].data[point][0], 
				3, line);
	}

	@Override
	protected CertificateValue setCalibrationBudget(int line, int index) {
		double[] uncerinity = findUncerinity(index, 0);
		setUncerinity(uncerinity, line);
		sheet.setValueAt(order.measurmets[index].average[0], 7 , line + 5);
        sheet.setValueAt(referenceValue.measurmets[index].average[0],
        		7 , line + 7);
        sheet.setValueAt(reference[index].correction, 7 , line +  9);
        sheet.setValueAt(order.device.resolution[0], 9 , line + 6);
        sheet.setValueAt(reference[index].uncertainty, 9, line + 9);
        sheet.setValueAt(reference[index].drift, 9, line + 10);
        sheet.setValueAt(chamber[index].correction, 9, line + 11);
        sheet.setValueAt(chamber[index].uncertainty, 9, line + 11);
        return setCertificateValue(index, uncerinity);
	}

	protected double[] findUncerinity(int index, int parametrIndex) {
		double[] uncerinity = new double[8];
        uncerinity[0] = order.measurmets[index].standardDeviation[parametrIndex];
        uncerinity[1] = Double.parseDouble(order.device.resolution[parametrIndex]) 
        		/ Math.sqrt(3);
        uncerinity[2] = referenceValue.measurmets[index].standardDeviation[parametrIndex];
        uncerinity[3] = 0.01 / Math.sqrt(3);
        uncerinity[4] = reference[index].getUncertainty(parametrIndex) / 2;
        uncerinity[5] = reference[index].getDrift(parametrIndex) / Math.sqrt(3);
        uncerinity[6] = chamber[index].correction / Math.sqrt(3);
        uncerinity[7] = chamber[index].uncertainty / 2;
		return uncerinity;
	}
	
	protected void setUncerinity(double[] uncerinity, int line) {
		for(int i = 0; i < uncerinity.length; i++){
            sheet.setValueAt(uncerinity[i], 13, line + 5 + i);
        }
	}
	
	private CertificateValue setCertificateValue(int index, double[] uncerinities) {
		CertificateValue pointValue = new CertificateValue();
		double uncerinity = findUncerinityAndRound(uncerinities);
        double referenceData = DataCalculation.roundTonumber(
        		referenceValue.measurmets[index].average[0]
        		+ reference[index].correction, round);
        double deviceValue =DataCalculation.roundTonumber(order.measurmets[index].average[0], round);
        pointValue.probeT = setNumber(referenceData);
        pointValue.deviceT = setNumber(deviceValue);
        pointValue.errorT = setNumber(deviceValue - referenceData);
        pointValue.uncertaintyT = setNumber(2 * uncerinity);
		return pointValue;
	}

	@Override
	protected void setCertificate() {
		certificate = new TemperatureCertificate();
	}

}