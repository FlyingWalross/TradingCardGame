package app.repositories;

import app.daos.*;
import app.dtos.*;
import app.enums.card_element;
import app.enums.card_type;
import app.exceptions.AlreadyExistsException;
import app.exceptions.NoPacksAvailableException;
import app.models.Card;
import app.models.Pack;
import app.models.Stack;
import app.models.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.*;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class PackRepository {
    PackDao packDao;
    CardDao cardDao;

    public Pack getById(Integer id) {
        try {
            return getPackDao().readById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Pack create(ArrayList<NewCard> newCards) throws AlreadyExistsException {
            Pack pack = new Pack();
        try {
            //convert new Cards to Database card model and save to database
            for (NewCard newCard : newCards) {
                card_element element = card_element.normal;
                if (newCard.getName().contains("Water")) {
                    element = card_element.water;
                } else if (newCard.getName().contains("Fire")) {
                    element = card_element.fire;
                }

                card_type type = card_type.valueOf(newCard.getName().replaceAll("(Water|Fire)", "").toLowerCase());
                Card card = getCardDao().create(new Card(
                        newCard.getId(),
                        newCard.getName(),
                        type,
                        element,
                        newCard.getDamage()
                ));
                pack.getCardIDs().add(card.getId());
            }

            return getPackDao().create(pack);
        } catch (SQLException e) {

            // Delete created cards if pack creation fails
            for(String cardId: pack.getCardIDs()) {
                try {
                    Card card = getCardDao().readById(cardId);
                    getCardDao().delete(card);
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }

            String sqlState = e.getSQLState();
            if (sqlState.equals("23000") || sqlState.equals("23505")) {
                throw new AlreadyExistsException("Card already exists");
            } else {
                // Other SQL exceptions
                throw new RuntimeException(e);
            }
        }
    }

    public PackDTO getRandomPack() throws NoPacksAvailableException {
        try {
            HashMap<Integer, Pack> packs = getPackDao().read();

            if(packs.isEmpty()){
                throw new NoPacksAvailableException("No packs available");
            }

            // Get a random entry from the HashMap
            Object[] keys = packs.keySet().toArray();
            Integer randomKey = (Integer) keys[new Random().nextInt(keys.length)];
            Pack randomPack = packs.get(randomKey);

            PackDTO randomPackDTO = new PackDTO(randomPack.getId());

            for(String cardID : randomPack.getCardIDs()) {
                Card card = getCardDao().readById(cardID);
                randomPackDTO.getCards().add(card);
            }

            return  randomPackDTO;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(PackDTO packDTO) {
        try {
            Pack pack = getPackDao().readById(packDTO.getId());
            getPackDao().delete(pack);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}