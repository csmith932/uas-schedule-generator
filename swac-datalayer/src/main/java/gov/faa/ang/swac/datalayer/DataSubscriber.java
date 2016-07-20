package gov.faa.ang.swac.datalayer;

public interface DataSubscriber 
{
	public void onLoad(Object source);
	public void onSave(Object source);
}
