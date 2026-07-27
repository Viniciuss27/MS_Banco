package vinix.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Getter
@EqualsAndHashCode(of = "id")
@Entity @Table(name = "tb_user")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id @Setter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	private String name;

	@Setter @Column(unique = true)
	private String email;

	@Setter
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	// recebe do JSON e não aparece na resposta
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tb_user_role",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User(Long id, String name, String email, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
				.map(x -> new SimpleGrantedAuthority(x.getRoleName()))
				.collect(Collectors.toList());
	}

	@Override // retorna o email como identificador do usuário
	public String getUsername() { return email; }

	@Override // conta não expirada
	public boolean isAccountNonExpired() { return true; }

	@Override // conta não bloqueada
	public boolean isAccountNonLocked() { return true;}

	@Override // senha não expirada
	public boolean isCredentialsNonExpired() { return true; }

	@Override // conta ativa
	public boolean isEnabled() { return true; }
}