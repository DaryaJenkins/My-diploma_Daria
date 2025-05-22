package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Locale;
import java.util.Random;

public class DataHelper {

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Random random = new Random();
    private static final int currentYear = java.time.Year.now().getValue() % 100;

    private DataHelper() {}

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

    private static String generateExpiredYear() {
        return String.valueOf(currentYear - 2);
    }

    private static String generateHolder() {
        return faker.name().fullName();
    }

    private static String generateCvv() {
        return String.format("%03d", random.nextInt(1000));
    }

    public static CardInfo generateValidApproveCard() {
        return new CardInfo("4444 4444 4444 4441",
                generateMonth(),
                String.valueOf(currentYear),
                generateHolder(),
                generateCvv());
    }

    public static CardInfo generateValidDeclineCard() {
        return new CardInfo("4444 4444 4444 4442",
                generateMonth(),
                String.valueOf(currentYear),
                generateHolder(),
                generateCvv());
    }

    public static CardInfo generateInvalidCardNumber() {
        String number = faker.number().digits(15);
        return new CardInfo(number,
                generateMonth(),
                String.valueOf(currentYear),
                generateHolder(),
                generateCvv());
    }

    public static CardInfo generateExpiredCard() {
        return new CardInfo("4444 4444 4444 4441",
                generateMonth(),
                generateExpiredYear(),
                generateHolder(),
                generateCvv());
    }
}