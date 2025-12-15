package mobile.jira.clonejira.service;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.repository.UserRepository;
import mobile.jira.clonejira.entity.*;

@Service
@RequiredArgsConstructor
public class JwtService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException{
        UUID uuid = UUID.fromString(uid);
        User user = userRepository.findById(uuid)
                    .orElseThrow(() -> new UsernameNotFoundException("User undefined!"));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUid().toString(),
            user.getEmail(),
            new ArrayList<>()
        );
    }
}
