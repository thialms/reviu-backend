package br.edu.fatecpg.reviu.repositories;

import br.edu.fatecpg.reviu.domain.card.Card;
import br.edu.fatecpg.reviu.domain.deck.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByDeckId(Long deckId);

    //CARDS A SEREM REVISADOS HOJE
    List<Card> findByDeckIdAndNextReviewLessThanEqual(Long deckId, LocalDate date);

}
