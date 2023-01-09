package app.repositories;

import app.daos.*;
import app.dtos.*;
import app.enums.card_element;
import app.enums.card_type;
import app.exceptions.AlreadyExistsException;
import app.exceptions.NoPacksAvailableException;
import app.models.Card;
import app.models.Pack;
import app.services.CardConversionService;
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
                Card card = CardConversionService.convertCard(newCard);
                getCardDao().create(card);
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

    public PackDTO getPack() throws NoPacksAvailableException {
        try {
            HashMap<Integer, Pack> packs = getPackDao().read();

            if(packs.isEmpty()){
                throw new NoPacksAvailableException("No packs available");
            }

            Pack openedPack = packs.get(packs.keySet().stream().min(Integer::compare).get());
            PackDTO openedPackDTO = new PackDTO(openedPack.getId());

            for(String cardID : openedPack.getCardIDs()) {
                Card card = getCardDao().readById(cardID);
                openedPackDTO.getCards().add(card);
            }

            return  openedPackDTO;
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