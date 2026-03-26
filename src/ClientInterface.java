
public interface ClientInterface {
	// Method VCController will call to notify the client
	void notify (String message);
	
    // Client sends data to VCController
    void sendDataToServer(VCController controller, Object data);
}
