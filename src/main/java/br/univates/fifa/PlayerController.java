package br.univates.fifa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fifa")
@Tag(name = "FIFA", description = "Performance e valor de mercado de jogadores")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @Operation(summary = "Insere um jogador")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insert(@RequestBody Player player) {
        if (player.playerName() == null || player.playerName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "player_name é obrigatório"));
        }
        if (!service.insert(player)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "player_id " + player.playerId() + " já existe"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    @Operation(summary = "Quantidade de jogadores")
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] count() {
        return service.count();
    }

    @Operation(summary = "Lista todos os jogadores")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] all() {
        return service.all();
    }

    @Operation(summary = "Busca um jogador pelo player_id")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> byId(@PathVariable int id) {
        return service.byId(id)
                .map(b -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"jogador não encontrado\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }

    @Operation(summary = "Jogadores por nacionalidade", description = "Ex.: /fifa/nat/Brazil (nationality = 'Brazil')")
    @GetMapping(value = "/nat/{exp}", produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] byNationality(@PathVariable String exp) {
        return service.byNationality(exp);
    }
}
