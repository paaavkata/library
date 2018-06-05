package bg.premiummobile.library.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bg.premiummobile.library.dto.ErrorDTO;
import bg.premiummobile.library.dto.LoggedInResponseDTO;
import bg.premiummobile.library.dto.UserDTO;
import bg.premiummobile.library.dto.UserInfoDTO;
import bg.premiummobile.library.exception.UserNotAuthorizedException;
import bg.premiummobile.library.exception.WrongPasswordException;
import bg.premiummobile.library.model.User;
import bg.premiummobile.library.service.UserService;
import bg.premiummobile.library.util.UserLevel;

@RestController
@RequestMapping("/user")
public class UserController {

	private UserService userService;
	
	@Autowired
	public UserController(UserService userService){
		this.userService = userService;
	}
	
	@PostMapping("/login")
	public LoggedInResponseDTO login(@Valid @RequestBody UserDTO dto){
		LoggedInResponseDTO returnDTO = userService.authenticate(dto.getUsername(), dto.getPassword().toCharArray());
	    return returnDTO;
	}
	
	@PostMapping("/register")
	public boolean register(@Valid @RequestBody UserDTO dto) {
		if(userService.register(dto.getUsername(), dto.getPassword().toCharArray())){
			return true;
		}
	    return false;
	}
	
	@PutMapping("/update")
	public User updateInfo(@RequestHeader String token, @RequestHeader long userId, @RequestBody UserInfoDTO user) {
		if(userService.hasAccess(token, userId, UserLevel.USER)){
			return userService.updateInfo(user, userId);
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	@GetMapping("/logout")
	public boolean logout(@RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.USER)){
			return userService.logout(userId);
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	@ExceptionHandler({ WrongPasswordException.class, UserNotAuthorizedException.class })
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleAuthorizationException(Exception e) {
        return new ErrorDTO(401, e.getMessage());
    }
	
}
