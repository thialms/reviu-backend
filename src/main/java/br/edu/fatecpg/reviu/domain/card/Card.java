package br.edu.fatecpg.reviu.domain.card;

import br.edu.fatecpg.reviu.domain.deck.Deck;
import br.edu.fatecpg.reviu.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private int repetitions;
    private int interval;
    private double easinessFactor;
    private LocalDate lastReview;
    private LocalDate nextReview;


    @ManyToOne
    @JoinColumn(name = "deck_id")
    private Deck deck;
}
