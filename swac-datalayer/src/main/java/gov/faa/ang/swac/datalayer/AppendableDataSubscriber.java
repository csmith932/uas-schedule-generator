package gov.faa.ang.swac.datalayer;

public interface AppendableDataSubscriber extends DataSubscriber {
	public void onAppend(Object source);

}
