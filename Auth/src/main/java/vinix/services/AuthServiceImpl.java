package vinix.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vinix.dto.request.LoginRequestDTO;
import vinix.dto.request.RegisterRequestDTO;
import vinix.dto.response.LoginResponseDTO;
import vinix.dto.response.UserResponseDTO;
import vinix.entities.Role;
import vinix.entities.User;
import vinix.mapper.UserMapper;
import vinix.repositories.RoleRepository;
import vinix.repositories.UserRepository;
import vinix.services.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	

	@Override
	public LoginResponseDTO login(LoginRequestDTO dto) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
		   String token = "00" ;// não criei o gerador de token ainda
	        return new LoginResponseDTO(token, token, 0);
	    }

	@Override
	public UserResponseDTO register(RegisterRequestDTO dto) {
		Role role = roleRepository.findByRoleName("ROLE_CLIENT")
		        .orElseThrow(() -> new ResourceNotFoundException(
		        		"Role padrão não encontrada: ROLE_CLIENT"));
		
		User user = new User();
		user.setName(dto.name());
		user.setEmail(dto.email());
		user.setPassword(passwordEncoder.encode(dto.password()));
		
		user.getRoles().add(role);
		
		User salvo = userRepository.save(user);
		
		return userMapper.toResponseDTO(salvo);
	}

}
