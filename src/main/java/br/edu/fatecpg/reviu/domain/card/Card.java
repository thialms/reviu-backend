package br.edu.fatecpg.reviu.domain.card;

import br.edu.fatecpg.reviu.domain.deck.Deck;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String frontText;
    private String backText;

    private String imageUrl;
    private String audioUrl;

    private Integer repetitions;
    private Integer interval;
    private Double difficulty;
    private Double stability;
    private Double retrievability;

    private LocalDate lastReview;
    private LocalDate nextReview;

    @ManyToOne
    @JoinColumn(name = "deck_id")
    private Deck deck;
}
