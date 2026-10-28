package br.univates.fifa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Camada de serviço com cache de respostas já serializadas (JSON em bytes).
 * O cache é invalidado a cada POST, então os dados nunca ficam desatualizados.
 * Isso reduz o tempo das consultas (critério de pontuação do API Battle).
 */
@Service
public class PlayerService {

    private final PlayerRepository repo;
    private final ObjectMapper mapper;

    private volatile byte[] countCache;
    private volatile byte[] allCache;
    private final Map<Integer, byte[]> idCache = new ConcurrentHashMap<>();
    private final Map<String, byte[]> natCache = new ConcurrentHashMap<>();

    public PlayerService(PlayerRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public byte[] count() {
        byte[] c = countCache;
        if (c == null) {
            c = json(Map.of("count", repo.count()));
            countCache = c;
        }
        return c;
    }

    public byte[] all() {
        byte[] c = allCache;
        if (c == null) {
            c = json(repo.findAll());
            allCache = c;
        }
        return c;
    }

    public Optional<byte[]> byId(int id) {
        byte[] c = idCache.get(id);
        if (c != null) return Optional.of(c);
        Optional<byte[]> found = repo.findById(id).map(this::json);
        found.ifPresent(b -> idCache.put(id, b));
        return found;
    }

    public byte[] byNationality(String nat) {
        String key = nat.toLowerCase();
        return natCache.computeIfAbsent(key, k -> json(repo.findByNationality(nat)));
    }

    public boolean insert(Player p) {
        boolean inserted = repo.insert(p);
        if (inserted) invalidate();
        return inserted;
    }

    public void invalidate() {
        countCache = null;
        allCache = null;
        idCache.clear();
        natCache.clear();
    }

    private byte[] json(Object o) {
        try {
            return mapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
