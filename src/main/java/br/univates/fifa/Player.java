package br.univates.fifa;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Mesma estrutura do CSV (nomes das colunas em snake_case no JSON). */
public record Player(
        @JsonProperty("player_id") int playerId,
        @JsonProperty("player_name") String playerName,
        @JsonProperty("age") int age,
        @JsonProperty("nationality") String nationality,
        @JsonProperty("club") String club,
        @JsonProperty("position") String position,
        @JsonProperty("overall_rating") int overallRating,
        @JsonProperty("potential_rating") int potentialRating,
        @JsonProperty("matches_played") int matchesPlayed,
        @JsonProperty("goals") int goals,
        @JsonProperty("assists") int assists,
        @JsonProperty("minutes_played") int minutesPlayed,
        @JsonProperty("market_value_million_eur") double marketValueMillionEur,
        @JsonProperty("contract_years_left") int contractYearsLeft,
        @JsonProperty("injury_prone") String injuryProne,
        @JsonProperty("transfer_risk_level") String transferRiskLevel
) {}
