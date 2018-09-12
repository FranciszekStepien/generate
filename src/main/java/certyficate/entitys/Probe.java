package certyficate.entitys;

public class Probe{
	private String model;
	private String type;
	private String producent;
	  
	public Probe() {
		super();
	}
	
	public Probe(String model) {
		setModel(model);
	}

	public void setModel(String model) {
		this.model = model;
	}
		  
	public String getModel() {
		return model;
	}
		
	public void setType(String type) {
		this.type = type;
	}
		  
	public String getType() {
		return type;
	}
		
	public void setProducent(String producent) {
		this.producent = producent;
	}
		  
	public String getProducent() {
		return producent;
	}
}