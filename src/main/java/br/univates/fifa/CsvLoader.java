package br.univates.fifa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Script de carga em lote (batch insert) do CSV.
 * Uso:  java -jar fifa-api.jar --app.load-csv=data/fifa.csv
 * Lê o CSV, insere em lotes de 500 e encerra a aplicação.
 */
@Component
public class CsvLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvLoader.class);
    private static final int BATCH = 500;

    private final PlayerRepository repo;
    private final ConfigurableApplicationContext ctx;
    private final String csv;

    public CsvLoader(PlayerRepository repo, ConfigurableApplicationContext ctx,
                     @Value("${app.load-csv:}") String csv) {
        this.repo = repo;
        this.ctx = ctx;
        this.csv = csv;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (csv == null || csv.isBlank()) return;

        long t0 = System.currentTimeMillis();
        int read = 0, inserted = 0;
        List<Player> lote = new ArrayList<>(BATCH);

        try (BufferedReader br = Files.newBufferedReader(Path.of(csv), StandardCharsets.UTF_8)) {
            br.readLine(); // cabeçalho
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                lote.add(parse(line));
                read++;
                if (lote.size() == BATCH) {
                    inserted += repo.insertBatch(lote);
                    lote.clear();
                }
            }
            if (!lote.isEmpty()) inserted += repo.insertBatch(lote);
        }

        log.info("Carga concluída: {} linhas lidas, {} inseridas em {} ms (total no banco: {})",
                read, inserted, System.currentTimeMillis() - t0, repo.count());
        System.exit(SpringApplication.exit(ctx));
    }

    private static Player parse(String line) {
        String[] c = line.split(",", -1);
        return new Player(
                Integer.parseInt(c[0].trim()), c[1].trim(), Integer.parseInt(c[2].trim()),
                c[3].trim(), c[4].trim(), c[5].trim(),
                Integer.parseInt(c[6].trim()), Integer.parseInt(c[7].trim()),
                Integer.parseInt(c[8].trim()), Integer.parseInt(c[9].trim()),
                Integer.parseInt(c[10].trim()), Integer.parseInt(c[11].trim()),
                Double.parseDouble(c[12].trim()), Integer.parseInt(c[13].trim()),
                c[14].trim(), c[15].trim());
    }
}
