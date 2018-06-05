package bg.premiummobile.library.model;

import java.util.Calendar;

public class CustomSession {

	private String token;
	private long userId;
	private long timestamp;
	
	public CustomSession(String token, long userId){
		this.token = token;
		this.userId = userId;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
