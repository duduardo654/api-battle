package br.univates.fifa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlayerRepository {

    private static final String COLS = "player_id, player_name, age, nationality, club, position, "
            + "overall_rating, potential_rating, matches_played, goals, assists, minutes_played, "
            + "market_value_million_eur, contract_years_left, injury_prone, transfer_risk_level";

    private static final String INSERT = "INSERT INTO players (" + COLS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final RowMapper<Player> MAPPER = (rs, i) -> new Player(
            rs.getInt("player_id"),
            rs.getString("player_name"),
            rs.getInt("age"),
            rs.getString("nationality"),
            rs.getString("club"),
            rs.getString("position"),
            rs.getInt("overall_rating"),
            rs.getInt("potential_rating"),
            rs.getInt("matches_played"),
            rs.getInt("goals"),
            rs.getInt("assists"),
            rs.getInt("minutes_played"),
            rs.getDouble("market_value_million_eur"),
            rs.getInt("contract_years_left"),
            rs.getString("injury_prone"),
            rs.getString("transfer_risk_level"));

    private final JdbcTemplate jdbc;

    public PlayerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long count() {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM players", Long.class);
        return n == null ? 0 : n;
    }

    public List<Player> findAll() {
        return jdbc.query("SELECT " + COLS + " FROM players ORDER BY player_id", MAPPER);
    }

    public Optional<Player> findById(int id) {
        return jdbc.query("SELECT " + COLS + " FROM players WHERE player_id = ?", MAPPER, id)
                .stream().findFirst();
    }

    public List<Player> findByNationality(String nationality) {
        return jdbc.query("SELECT " + COLS + " FROM players WHERE lower(nationality) = lower(?) ORDER BY player_id",
                MAPPER, nationality);
    }

    /** @return true se inseriu, false se o player_id já existia. */
    public boolean insert(Player p) {
        return jdbc.update(INSERT + " ON CONFLICT (player_id) DO NOTHING", args(p)) == 1;
    }

    /** Inserção em lote (usada pela carga do CSV). */
    public int insertBatch(List<Player> players) {
        List<Object[]> batch = players.stream().map(PlayerRepository::args).toList();
        int[] res = jdbc.batchUpdate(INSERT + " ON CONFLICT (player_id) DO NOTHING", batch);
        int ok = 0;
        for (int r : res) if (r > 0 || r == java.sql.Statement.SUCCESS_NO_INFO) ok++;
        return ok;
    }

    private static Object[] args(Player p) {
        return new Object[]{p.playerId(), p.playerName(), p.age(), p.nationality(), p.club(), p.position(),
                p.overallRating(), p.potentialRating(), p.matchesPlayed(), p.goals(), p.assists(),
                p.minutesPlayed(), p.marketValueMillionEur(), p.contractYearsLeft(), p.injuryProne(),
                p.transferRiskLevel()};
    }
}
