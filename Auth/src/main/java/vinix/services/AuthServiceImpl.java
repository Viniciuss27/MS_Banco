package vinix.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vinix.config.JwtService;
import vinix.dto.request.LoginRequestDTO;
import vinix.dto.request.RegisterRequestDTO;
import vinix.dto.response.LoginResponseDTO;
import vinix.dto.response.UserResponseDTO;
import vinix.entities.Role;
import vinix.entities.User;
import vinix.mapper.UserMapper;
import vinix.repositories.RoleRepository;
import vinix.repositories.UserRepository;
import vinix.resources.exceptions.DuplicateEmailException;
import vinix.services.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final JwtService jwtService;
	

	@Override
	public LoginResponseDTO login(LoginRequestDTO dto) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
		   String token = jwtService.gerateToken(dto.email());
	        return new LoginResponseDTO(token, "Bearer", jwtService.getExpiration());
	    }

	@Override
	@Transactional
	public UserResponseDTO register(RegisterRequestDTO dto) {
	    if (userRepository.findByEmail(dto.email()).isPresent()) {
	        throw new DuplicateEmailException(
	        		"Já existe uma conta cadastrada com o email " + dto.email());
	    }

	    Role role = roleRepository.findByRoleName("ROLE_CLIENT")
	            .orElseThrow(() -> new ResourceNotFoundException(
	            		"Role padrão não encontrada: ROLE_CLIENT"));

	    User user = userMapper.toEntity(dto);
	    user.setPassword(passwordEncoder.encode(dto.password()));
	    user.getRoles().add(role);

	    return userMapper.toResponseDTO(userRepository.save(user));
	}

}
