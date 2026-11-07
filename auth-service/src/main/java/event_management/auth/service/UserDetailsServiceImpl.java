package event_management.auth.service;

import event_management.auth.model.Users;
import event_management.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Carregando usuário para autenticação: {}", username);

        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado");
                });

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getSenha(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getNome()))
                        .toList()
        );
    }
}
