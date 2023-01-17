package app.repositories;

import app.daos.CardDao;
import app.daos.TradeDao;
import app.models.Trade;
import app.exceptions.AlreadyExistsException;
import app.dtos.TradeDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class TradeRepository {
    TradeDao tradeDao;
    CardDao cardDao;

    public TradeDTO getById(String id) {
        try {
            Trade trade = getTradeDao().readById(id);
            if(trade == null) {
                return null;
            }
            return new TradeDTO(
                    trade.getId(),
                    trade.getUsername(),
                    getCardDao().readById(trade.getCardId()),
                    trade.getType(),
                    trade.getMin_damage()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Trade> getAll() {
        try {
            return new ArrayList<>(getTradeDao().read().values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void create(Trade trade) throws AlreadyExistsException {
        try {
            getTradeDao().create(trade);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState.equals("23000") || sqlState.equals("23505")) { //duplicate primary key
                throw new AlreadyExistsException("Card already exists");
            } else {
                // Other SQL exceptions
                throw new RuntimeException(e);
            }
        }
    }

    public void delete(TradeDTO tradeDTO) {
        try {
            Trade trade = getTradeDao().readById(tradeDTO.getId());
            getTradeDao().delete(trade);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}