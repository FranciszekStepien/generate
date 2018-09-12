package certyficate.GUI.infrared;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import certyficate.entitys.*;
import certyficate.property.CalibrationData;

@SuppressWarnings("serial")
public class InfraredParametrs extends JDialog {
	public static final String PANEL_NAME = "dane o badanych pirometrach";
	public static final String BUTTON_LABEL = "zatwierdź";
	public static final String NUMBER_SEPARATOR = " ÷ ";
	
	public static final int WIDTH = 700;
	public static final int HIGHT = 600;
	public static final int DEVICES_PER_LINE = 3;
	
	private PyrometerPanel[] pyrometers;
	
	private List<Order> data;
	
	private int devicesPerWindow;
	private int numberOfOrders;
	
	private GridBagConstraints constrain;
	
	public InfraredParametrs(Frame owner){
		super(owner, true);
		BlackBodyData.setBlackBodyGenerators();
		findPanelData();
		setElements();
		setWindow();
		setPanelSettings();
	}
	
	public void setIRData(IRData pyrometerData) {
		for(PyrometerPanel pyrometer: pyrometers) {
			pyrometer.checkData(pyrometerData);
		}
	}

	private void findPanelData() {
		data = CalibrationData.orders;
		numberOfOrders = data.size();
		checkMaxCalibrationPoints();		
	}

	private void checkMaxCalibrationPoints() {
		int max = findMaxCalibrationPoints();
		if(max > 3) {
			devicesPerWindow = DEVICES_PER_LINE;
		} else {
			devicesPerWindow = 2 * DEVICES_PER_LINE;
		}
	}

	private int findMaxCalibrationPoints() {
		int max = 0;
		for(Order calibration: data) {
			int calibrationPoints = calibration.getPointLength();
			max = Math.max(max, calibrationPoints);
		}
		return max;
	}

	private void setPanelSettings() {
		Dimension size = new Dimension(WIDTH, HIGHT);
		setSize(size);
		setTitle(PANEL_NAME);
		setVisible(true);
	}
	
	private void setElements() {
		pyrometers = new PyrometerPanel[data.size()];
		constrain = new GridBagConstraints();
	}

	private JButton getAcceptButton() {
		JButton accept = new JButton(BUTTON_LABEL);
		accept.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){		
				setData();
				close();
			}
		});
		return accept;
	}
	
	private void setData() {
		for(PyrometerPanel pyrometer: pyrometers) {
			pyrometer.setPyrometerData();
		}
	}
	
	private void close(){
		this.dispose();
	}
	
	private void setWindow() {
		if(numberOfOrders > devicesPerWindow) {
			setTabbedPane();
		} else {
			setFirstPanel();
		}
	}

	private void setTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		for(int i = 0; i < numberOfOrders; i += devicesPerWindow){
			String TabName = setName(i);
			tabbedPane.addTab(TabName, getPanel(i));
		}
		add(tabbedPane);
	}

	private String setName(int index) {
		int startOfName = index + 1;
		int endOfName = findMinimum(index);
		StringBuilder bulid = new StringBuilder();
		bulid.append(startOfName);
		bulid.append(NUMBER_SEPARATOR);
		bulid.append(endOfName);
		return bulid.toString();
	}

	private int findMinimum(int index) {
		return Math.min(index + devicesPerWindow, numberOfOrders);
	}

	private void setFirstPanel() {
		add(getPanel(0));		
	}

	private JPanel getPanel(int index) {
		int lastObjectInPanel = findMinimum(index);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		for(int i = index; i < lastObjectInPanel; i++){
			setPyrometrPanel(panel, i);
		}
		addAcceptButton(panel);
		return panel;
	}

	private void setPyrometrPanel(JPanel panel, int index) {
		Order certificate = data.get(index);
		pyrometers[index] =
				new PyrometerPanel(certificate, this);
		constrain.gridy = index / DEVICES_PER_LINE;
		constrain.gridx = index % DEVICES_PER_LINE;
		panel.add(pyrometers[index], constrain);
	}
	
	private void addAcceptButton(JPanel panel) {
		constrain.gridy++;
		constrain.gridx = 0;
		constrain.gridwidth = 3;
		panel.add(getAcceptButton(), constrain);
		constrain.gridwidth = 1;
	}
}

