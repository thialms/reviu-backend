package br.edu.fatecpg.reviu.services;

import br.edu.fatecpg.reviu.domain.card.Card;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * FsrsService - versão ajustada para comportamento prático e escalável do FSRS 4.x
 *
 * Rating esperado (pela sua API): 1 = Again, 2 = Hard, 3 = Good, 4 = Easy
 */
@Service
public class FsrsService {

    // ---------- PARÂMETROS TUNÁVEIS ----------
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    // valores iniciais para S quando o card é novo (experiência prática)
    private static final double INITIAL_S_AGAIN = 0.1;
    private static final double INITIAL_S_HARD  = 1.5;
    private static final double INITIAL_S_GOOD  = 4.0;
    private static final double INITIAL_S_EASY  = 10.0;

    // estabilidade mínima para evitar divisão por zero
    private static final double MIN_STABILITY = 0.1;

    // quanto reduz a stability em lapses (Again)
    private static final double POST_LAPSE_PENALTY = 0.25;

    // quão sensível é a dificuldade ao rating (ajuste fino)
    private static final double DIFFICULTY_UPDATE_FACTOR = 0.05;

    // base para crescimento da stability (ajuste fino)
    private static final double BASE_GROWTH = 3.0;

    // multiplicadores por rating
    private static final double HARD_MULTIPLIER = 0.6;
    private static final double GOOD_MULTIPLIER = 1.0;
    private static final double EASY_MULTIPLIER = 1.6;

    private static final double MIN_DIFFICULTY = 0.0;
    private static final double MAX_DIFFICULTY = 1.0;

    // ------------------------------------------

    /**
     * Atualiza o card usando lógica inspirada no FSRS (ajustada para ser prática).
     * rating: 1=Again, 2=Hard, 3=Good, 4=Easy
     */
    public void processFsrs(Card card, int rating) {
        if (rating < 1 || rating > 4) throw new IllegalArgumentException("rating must be 1..4");

        // valores atuais (aceita nulls)
        Double maybeD = card.getDifficulty();
        Double maybeS = card.getStability();
        Double maybeR = card.getRetrievability();

        double d = maybeD == null ? 0.3 : maybeD;
        double s = maybeS == null ? 0.0 : maybeS;

        // dias desde última revisão (usa timezone do sistema)
        LocalDate now = LocalDate.now(ZONE);
        LocalDate last = card.getLastReview();
        long daysSinceLast = (last == null) ? 1 : ChronoUnit.DAYS.between(last, now);
        if (daysSinceLast < 1) daysSinceLast = 1;

        boolean isNew = (s <= 0.0);

        // ----------------- 1) calcular retrievability r = exp(-t / S) -----------------
        double r;
        if (s <= 0.0) {
            r = 1.0; // se nunca revisado, assumimos que lembra agora
        } else {
            r = Math.exp(-((double) daysSinceLast) / Math.max(s, MIN_STABILITY));
            r = clamp(r, 0.0, 1.0);
        }

        // ----------------- 2) atualizar dificuldade D -----------------
        // Queremos: rating alto (4) -> diminuir dificuldade; rating baixo (1) -> aumentar.
        // delta = (3 - rating) * factor  -> rating=4 => -factor (diminui), rating=1 => +2*factor (aumenta)
        double deltaD = (3 - rating) * DIFFICULTY_UPDATE_FACTOR;
        double newD = clamp(d + deltaD, MIN_DIFFICULTY, MAX_DIFFICULTY);

        // ----------------- 3) atualizar stability S -----------------
        double newS;
        if (isNew) {
            // inicia S dependendo do rating (valores maiores para Good/Easy)
            switch (rating) {
                case 1: newS = INITIAL_S_AGAIN; break;
                case 2: newS = INITIAL_S_HARD;  break;
                case 3: newS = INITIAL_S_GOOD;  break;
                case 4: newS = INITIAL_S_EASY;  break;
                default: newS = INITIAL_S_GOOD;
            }
            // ajuste leve por dificuldade: items muito difíceis começam um pouco mais baixos
            newS = Math.max(MIN_STABILITY, newS * (1.0 - 0.3 * newD));
        } else {
            if (rating == 1) {
                // lapse: penalidade forte
                newS = Math.max(MIN_STABILITY, s * POST_LAPSE_PENALTY);
                // aumentar dificuldade um pouco ao falhar
                newD = clamp(newD + 0.04, MIN_DIFFICULTY, MAX_DIFFICULTY);
            } else {
                // sucesso: crescimento multiplicativo baseado em difficulty e retrievability (1 - r)
                double difficultyEffect = Math.exp(1.0 - newD); // menor D -> maior efeito
                double base = BASE_GROWTH * difficultyEffect;

                double ratingMultiplier = switch (rating) {
                    case 2 -> HARD_MULTIPLIER; // Hard
                    case 3 -> GOOD_MULTIPLIER; // Good
                    case 4 -> EASY_MULTIPLIER; // Easy
                    default -> GOOD_MULTIPLIER;
                };

                // quanto maior (1 - r) (ou seja, quanto mais "fraco" estava), maior o ganho
                double gainFactor = base * (1.0 - r) * ratingMultiplier;

                // aplica crescimento
                newS = s * (1.0 + gainFactor);

                // garante mínimo
                if (Double.isNaN(newS) || newS < MIN_STABILITY) newS = MIN_STABILITY;
            }
        }

        // ----------------- 4) calcular intervalo (em dias) -----------------
        // Usa ceil(newS) para agendamentos práticos (S contínuo, interval em dias inteiros)
        int interval = (int) Math.max(1, Math.ceil(newS));

        // ----------------- 5) atualizar o card -----------------
        card.setDifficulty(newD);
        card.setStability(newS);
        card.setRetrievability(r);
        card.setInterval(interval);
        card.setRepetitions(card.getRepetitions() == null ? 1 : card.getRepetitions() + 1);
        card.setLastReview(now);
        card.setNextReview(now.plusDays(interval));
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
