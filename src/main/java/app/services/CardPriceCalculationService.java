package app.services;

import app.models.Card;

import static app.enums.card_type.*;

//calculates the price for cards when selling to the shop
public class CardPriceCalculationService {
    public static int calculatePrice(Card card) {

        double price = 0;

        //add price for damage
        if(card.getDamage() < 10.1f) {
            price += 1;
        } else if(card.getDamage() < 15.1f) {
            price += 2;
        } else if (card.getDamage() < 20.1f) {
            price += 3;
        } else if (card.getDamage() < 25.1f) {
            price += 4;
        } else if (card.getDamage() < 30.1f) {
            price += 5;
        } else if (card.getDamage() < 35.1f) {
            price += 6;
        } else if (card.getDamage() < 40.1f) {
            price += 7;
        } else if (card.getDamage() < 45.1f) {
            price += 8;
        } else if (card.getDamage() < 50.1f) {
            price += 9;
        } else {
            price += 10;
        }

        if(card.getType() == spell) {
            price += 1;
        }

        if(card.getType() == dragon || card.getType() == wizard) {
            price += 2;
        }

        return (int) price;
    }
}
