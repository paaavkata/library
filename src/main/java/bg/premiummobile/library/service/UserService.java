package bg.premiummobile.library.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import bg.premiummobile.library.data.UserRepository;
import bg.premiummobile.library.dto.LoggedInResponseDTO;
import bg.premiummobile.library.dto.UserInfoDTO;
import bg.premiummobile.library.exception.EntityNotFoundException;
import bg.premiummobile.library.exception.UsernameAlreadyTakenException;
import bg.premiummobile.library.exception.WrongPasswordException;
import bg.premiummobile.library.model.CustomSession;
import bg.premiummobile.library.model.User;

@Service
public class UserService {

	private UserRepository userRepository;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<Long, User> users;
	
	private List<CustomSession> sessions;
	
	private final int logRounds = 10;
	
	@Value("${sessionDurationMinutes}")
	private int sessionDurationMinutes;
	
	@Autowired
	public UserService(UserRepository userRepository){
		this.userRepository = userRepository;
		users = new HashMap<>();
		for(User u : userRepository.findAll()){
			this.users.put(u.getId(), u);
		}
		this.sessions = new ArrayList<>();
	}
	
	public LoggedInResponseDTO authenticate(String username, char[] password) {
		for(User u : users.values()){
			if(u.getUsername().equals(username)){
				if(verifyHash(password, u.getPassword())){
					
					for(CustomSession session : sessions){
						if(session.getUserId() == u.getId()){
							return new LoggedInResponseDTO(session.getToken(), u.getUsername(), u.getId());
						}
					}
					
					String token = UUID.randomUUID().toString().toUpperCase();
					sessions.add(new CustomSession(token, u.getId()));
					logger.info("User with id " + u.getId() + " logged in.");
					return new LoggedInResponseDTO(token, u.getUsername(), u.getId());
				}
			}
		}
		throw new WrongPasswordException();
	}

	public boolean register(String username, char[] password) {
		
		for(User u : users.values()){
			if(username.equals(u.getUsername())){
				throw new UsernameAlreadyTakenException();
			}
		}
		
		String hashedPassword = hash(password);
		
		User newUser = userRepository.save(new User(username, hashedPassword));
		if(null != newUser){
			users.put(newUser.getId(), newUser);
			logger.info("New user with username " + newUser.getUsername() + " created.");
			return true;
		}
		else{
			return false;
		}
	}

	public User updateInfo(UserInfoDTO user, long userId) {
		User oldUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(userId)));
		oldUser.setEmail(user.getEmail());
		oldUser.setName(user.getName());
		userRepository.save(oldUser);
		users.put(oldUser.getId(), oldUser);
		return oldUser;
	}

	public boolean hasAccess(String token, long id, int level){
		for(CustomSession s : sessions){
			if(s.getToken().equals(token)){
				if(s.getUserId() == id){
					if(users.get(id).getLevel() >= level){
						s.setTimestamp(Calendar.getInstance().getTimeInMillis());
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private String hash(char[] password) {
		String pass = BCrypt.hashpw(new String(password), BCrypt.gensalt(logRounds));
		
		Arrays.fill(password, Character.MIN_VALUE);
		
        return pass;
    }
	
	private boolean verifyHash(char[] password, String hash) {
		boolean isEqual = BCrypt.checkpw(new String(password), hash);
		
		Arrays.fill(password, Character.MIN_VALUE);
		
	    return isEqual;
	}
	
	@Scheduled(fixedRate = 1000)
	private void expireSessions(){
		long currentTime = Calendar.getInstance().getTimeInMillis();
		for(CustomSession s : sessions){
			if((s.getTimestamp() + sessionDurationMinutes*60) > currentTime){
				logger.info("Session for user with id " + s.getUserId() + " expired");
				sessions.remove(s);
			}
		}
	}

	public boolean logout(long userId) {
		for(CustomSession s : sessions){
			if(userId == s.getUserId()){
				sessions.remove(s);
				return true;
			}
		}
		return false;
	}
}
