package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Locale;
import java.util.Random;

public class DataHelper {

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Random random = new Random();

    private DataHelper() {
    }

    @Value
    public static class CardInfo {
        String number;
        String month;
        String year;
        String holder;
        String cvv;
    }

    private static String generateMonth() {
        return String.format("%02d", 1 + random.nextInt(12));
    }

    private static String generateValidYear() {
        int minYear = 26;
        return String.format("%02d", minYear + random.nextInt(5));
    }

    private static String generateExpiredYear() {
        int minYear = 24;
        return String.format("%02d", minYear - random.nextInt(3));
    }

    private static String generateHolder() {
        return faker.name().fullName();
    }

    private static String generateCvv() {
        return String.format("%03d", random.nextInt(1000));
    }

    public static CardInfo getApprovedCard() {
        return new CardInfo(
                "4444444444444441",
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getDeclinedCard() {
        return new CardInfo(
                "4444444444444442",
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    private static String getAnyValidCardNumber() {
        return random.nextBoolean() ? "4444444444444441" : "4444444444444442";
    }

    // ~ Валидация номера карты ~

    public static CardInfo getCardNumberWith15Digits() {
        return new CardInfo(
                faker.number().digits(15),
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardNumberWith17Digits() {
        return new CardInfo(
                faker.number().digits(17),
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardNumberWithLetters() {
        return new CardInfo(
                "ABCD #### #### ####",
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardNumberWithSymbols() {
        return new CardInfo(
                "^%!$ #### #### ####",
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardWithEmptyNumber() {
        return new CardInfo(
                "",
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    // ~ Валидация полей «Месяц» и «Год» ~

    public static CardInfo getCardWithInvalidMonth() {
        return new CardInfo(
                getAnyValidCardNumber(),
                faker.letterify("??"),
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardWithEmptyMonth() {
        return new CardInfo(
                getAnyValidCardNumber(),
                "",
                generateValidYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardWithExpiredYear() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateExpiredYear(),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardWithInvalidYear() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                faker.letterify("??"),
                generateHolder(),
                generateCvv()
        );
    }

    public static CardInfo getCardWithEmptyYear() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                "",
                generateHolder(),
                generateCvv()
        );
    }

    // ~ Валидация имени держателя карты ~

    public static CardInfo getCardWithDigitsInHolder() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                generateHolder() + "123",
                generateCvv()
        );
    }

    public static CardInfo getCardWithSymbolsInHolder() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                generateHolder() + "!@^%",
                generateCvv()
        );
    }

    public static CardInfo getCardWithOneLetterHolder() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                faker.letterify("?"),
                generateCvv()
        );
    }

    public static CardInfo getCardWithEmptyHolder() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                "",
                generateCvv()
        );
    }

    // ~ Валидация CVV ~

    public static CardInfo getCardWithCvv2Digits() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                faker.number().digits(2)
        );
    }

    public static CardInfo getApprovedCardWithCvv4Digits() {
        return new CardInfo(
                "4444444444444441",
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                faker.number().digits(4)
        );
    }

    public static CardInfo getDeclinedWithCvv4Digits() {
        return new CardInfo(
                "4444444444444442",
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                faker.number().digits(4));
    }

    public static CardInfo getCardWithLetterInCvv() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                faker.number().digits(2) + faker.letterify("?")
        );
    }

    public static CardInfo getCardWithSymbolInCvv() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                faker.number().digits(2) + "@"
        );
    }

    public static CardInfo getCardWithEmptyCvv() {
        return new CardInfo(
                getAnyValidCardNumber(),
                generateMonth(),
                generateValidYear(),
                generateHolder(),
                ""
        );
    }
}