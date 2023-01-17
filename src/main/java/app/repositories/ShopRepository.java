package app.repositories;

import app.daos.CardDao;
import app.daos.ShopDao;
import app.dtos.NewCard;
import app.dtos.NewShopCard;
import app.dtos.ShopCardDTO;
import app.exceptions.AlreadyExistsException;
import app.models.Card;
import app.models.ShopCard;
import app.services.CardConversionService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class ShopRepository {
    ShopDao shopDao;
    CardDao cardDao;

    public ShopCardDTO getById(String id) {
        try {
            ShopCard shopCard = getShopDao().readById(id);
            if(shopCard == null) {
                return null;
            }
            Card card = getCardDao().readById(shopCard.getCardId());
            return new ShopCardDTO(card, shopCard.getPrice());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<ShopCardDTO> getAll() {
        try {
            HashMap<String, ShopCard> shopCards = getShopDao().read();
            if(shopCards == null) {
                return null;
            }

            ArrayList<ShopCardDTO> shopCardDTOs = new ArrayList<>();

            for(ShopCard shopCard : shopCards.values()) {
                Card card = getCardDao().readById(shopCard.getCardId());
                shopCardDTOs.add(new ShopCardDTO(card, shopCard.getPrice()));
            }
            return shopCardDTOs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createWithNewCard(NewShopCard newShopCard) throws AlreadyExistsException { //admin can create a new card and offer it in shop
        try {

            //convert newCard to Database card model
            Card card = CardConversionService.convertCard(new NewCard(
                    newShopCard.getId(),
                    newShopCard.getName(),
                    newShopCard.getDamage()
            ));

            //save Card to database
            getCardDao().create(card);

            ShopCard shopCard = new ShopCard(
                    card.getId(),
                    newShopCard.getPrice()
            );

            //save shop card to database
            getShopDao().create(shopCard);

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState.equals("23000") || sqlState.equals("23505")) {
                throw new AlreadyExistsException("Card already exists");
            } else {
                // Other SQL exceptions
                throw new RuntimeException(e);
            }
        }
    }

    public void createWithExistingCard(Card card, int price) throws AlreadyExistsException { //users can sell their existing cards to shop
        try {

            ShopCard shopCard = new ShopCard(
                    card.getId(),
                    price
            );

            //save shop card to database
            getShopDao().create(shopCard);

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState.equals("23000") || sqlState.equals("23505")) {
                throw new AlreadyExistsException("Card already exists");
            } else {
                // Other SQL exceptions
                throw new RuntimeException(e);
            }
        }
    }

    public void delete(ShopCardDTO shopCardDTO) {
        try {
            getShopDao().delete(new ShopCard(shopCardDTO.getCard().getId(), shopCardDTO.getPrice()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}