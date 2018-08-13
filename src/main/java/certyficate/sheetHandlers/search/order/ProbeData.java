package certyficate.sheetHandlers.search.order;

import java.util.Map;

import org.jopendocument.dom.spreadsheet.Sheet;

import certyficate.entitys.Probe;
import certyficate.property.CalibrationData;

public class ProbeData {
	final private static String PROBES_LABEL = "Sondy";
	private static final String EMPTY_CELL = "";
	
	private static Sheet sheet;
	private static Map<String, Probe> data;
	
	public static void findProbeData(Map<String, Probe> probesData) {
		sheet = CalibrationData.spreadSheet.getSheet(PROBES_LABEL);
		data = probesData;
		findProbes();
	}

	private static void findProbes() {
		int line = 1;
		while(!EMPTY_CELL.equals(sheet.getValueAt(0, line))){		
			checkModel(line);		
			line++;
		}
	}

	private static void checkModel(int line) {
		String  model = sheet.getValueAt(0, line).toString();
		if(data.containsKey(model)) {
			addProbe(line);
		}
		line++;
	}

	private static void addProbe(int line) {
		Probe probe = new Probe();
		probe.setType(sheet.getValueAt(1, line).toString());
		probe.setProducent(sheet.getValueAt(2, line).toString());
		addProbe(probe, line);
	}

	private static void addProbe(Probe probe, int line) {
		String  model = sheet.getValueAt(0, line).toString();
		probe.setModel(model);	
		data.put(model, probe);	
	}
}
