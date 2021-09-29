package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.exception.CardNotFoundException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.repository.CardRepository;
import org.example.app.repository.UserRepository;
import org.example.framework.security.Roles;

import java.util.List;

@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public List<Card> getAllByOwnerId(long ownerId) {
        return cardRepository.getAllByOwnerId(ownerId);
    }

    /**
     * Find card from db if user is owner or he has role ROLE_ADMIN
     *
     * @param currentUser user who done request
     * @param cardId      id of card which we should find
     * @return Card founded card or UOE
     */
    public Card getById(User currentUser, long cardId) {
        final User cardUser = userRepository.getCardOwnerById(cardId).orElseThrow(UserNotFoundException::new);
        final var foundedUser = userRepository.findByUsernameWithRole(currentUser.getUsername());
        if (cardUser.getId() == currentUser.getId()
                || (foundedUser.isPresent()
                && !foundedUser.get().getRole().isEmpty()
                && foundedUser.get().getRole().equals(Roles.ROLE_ADMIN))) {
            return cardRepository.getCardById(cardId).orElseThrow(CardNotFoundException::new);
        } else {
            throw new UnsupportedOperationException("User not owner or don't have admin role!");
        }
    }

    public Card createNewCard(User currentUser, long balance) {
        return cardRepository.createNewCard(currentUser, balance).orElseThrow(() -> new CardNotFoundException("Cant create card!"));
    }

    public int blockCard(User currentUser, String cardNumber) {
        if (cardRepository.getAllByOwnerId(currentUser.getId()).contains(cardRepository.getCardByNumber(cardNumber).orElseThrow(CardNotFoundException::new))) {
            return cardRepository.blockCardByNumber(cardNumber);
        } else {
            throw new CardNotFoundException();
        }
    }

    public Card doOrder(User currentUser, String fromCardNumber, String toCardNumber, long money) {
        final User cardUser = userRepository.getCardOwnerByNumber(fromCardNumber).orElseThrow(UserNotFoundException::new);
        if (cardUser.getId() == currentUser.getId()) {
            final var from = cardRepository.getCardByNumber(fromCardNumber).orElseThrow(CardNotFoundException::new);
            final var to = cardRepository.getCardByNumber(toCardNumber).orElseThrow(CardNotFoundException::new);
            if (from.getBalance() >= money) {
                return cardRepository.doOrder(from, to, money);
            } else {
                throw new UnsupportedOperationException("User dont have enough money for order!");
            }
        } else {
            throw new UnsupportedOperationException("User not owner or don't have admin role!");
        }
    }
}
