package gov.faa.ang.swac.datalayer;

public interface DataPublisher 
{
	public void subscribe(DataSubscriber listener);
	public void unsubscribe(DataSubscriber listener);
}
