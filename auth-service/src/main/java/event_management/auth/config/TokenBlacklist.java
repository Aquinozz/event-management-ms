package event_management.auth.config;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void invalidate(String token) {
        blacklist.add(token);
    }

    public boolean isInvalid(String token) {
        return blacklist.contains(token);
    }
}
