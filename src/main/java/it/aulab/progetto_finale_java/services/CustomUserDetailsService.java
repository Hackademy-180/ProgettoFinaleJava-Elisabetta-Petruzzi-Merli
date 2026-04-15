package it.aulab.progetto_finale_java.services;

import java.util.Collection;
import java.util.stream.Collectors;

import it.aulab.progetto_finale_java.models.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("Invalid credentials");
        }
        return new CustomUserDetails(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            // sostituiamo la funzione getAuthorities con mapRolesToAuthorities...
            // getAuthorities()
            mapRolesToAuthorities(user.getRoles())
        );
    }
// anche qui sostituire getAuthorities con mapRolesAuthorities:
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
    }
}
