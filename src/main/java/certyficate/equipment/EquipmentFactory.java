package certyficate.equipment;

import java.io.IOException;

import certyficate.equipment.calculation.Calculate;
import certyficate.equipment.calculation.CalculateRh;
import certyficate.equipment.type.Equipment;
import certyficate.equipment.type.RhProbe;
import certyficate.equipment.type.TProbe;
import certyficate.files.PathCreator;

public class EquipmentFactory {
	private Equipment equipment;
	
	public Equipment getEquipment(EquipmentType equipmentType)
			throws IOException {
		findAndGetEquipment (equipmentType);
		return equipment;
	}

	private void findAndGetEquipment(EquipmentType equipmentType) 
			throws IOException {
		switch(equipmentType) {
		case TEMPERATURE_REFERENCE:
			setTemperatureReference();
			break;
		case HUMIDITY_REFERENCE:
			setHumidityReference();
			break;
		case INFRARED_REFERENCE:
			setInfraredReference();
			break;
		case CHAMBER_TEMPERATURE:
			setChamberTemperature();
			break;
		case CHAMBER_HUMIDITY:
			setChamberHumidity();
			break;
		default:
			setBlackBodyGenerator();
			break;
		}
	}

	private void setTemperatureReference() throws IOException {
		String path = PathCreator.filePath("");
		equipment = new TProbe(path);
		equipment.calculate = new Calculate();
	}
	
	private void setHumidityReference() throws IOException {
		String path = PathCreator.filePath("");
		equipment = new RhProbe(path);
		equipment.calculate = new CalculateRh();
	}
	
	private void setInfraredReference() throws IOException {
		String path = PathCreator.filePath("");
		equipment = new TProbe(path);
	}
	
	private void setChamberTemperature() throws IOException {
		String path = PathCreator.filePath("");
		//TODO chamber method
	}
	
	private void setChamberHumidity() throws IOException {
		String path = PathCreator.filePath("");
		//TODO chamber method
	}

	private void setBlackBodyGenerator() throws IOException {
		String path = PathCreator.filePath("");
		//TODO black body method
	}
	
}
