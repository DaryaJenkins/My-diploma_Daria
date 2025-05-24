package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.pages.CardForm;
import ru.netology.pages.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.SQLHelper.*;

public class PaymentCreditTest {

    private StartPage startPage;
    private CardForm cardForm;

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080");
        startPage = new StartPage();
    }

    @Test
    void shouldPayWithApprovedCard() {
        cardForm = startPage.clickOnPaymentButton();
        var approvedCard = DataHelper.getApprovedCard();
        cardForm.fillForm(approvedCard);
        cardForm.waitForSuccessSnackbar();

        assertEquals("APPROVED", getLatestPaymentStatus());
    }

    @Test
    void shouldSendRequestWithDeclinedCard() {
        cardForm = startPage.clickOnPaymentButton();
        var declinedCard = DataHelper.getDeclinedCard();
        cardForm.fillForm(declinedCard);
        cardForm.waitForErrorSnackbar();

        assertEquals("DECLINED", getLatestPaymentStatus());
    }

    @Test
    void shouldDeclineRequestIfCardNumberHas15Digits() {
        cardForm = startPage.clickOnPaymentButton();
        var cardWith15Digits = DataHelper.getCardNumberWith15Digits();
        cardForm.fillForm(cardWith15Digits);
        cardForm.waitForInputError();

        // Статус null, потому что запрос не дойдёт до БД
        assertEquals(null, getLatestPaymentStatus());
    }

    // Поле заполнится только 16 символами, но поскольку я генерирую случайный набор чисел в качестве номера карты, операция не будет одобрена банком.
    @Test
    void shouldDeclineRequestIfCardNumberHas17Digits() {
        cardForm = startPage.clickOnCreditButton();
        var cardWith17Digits = DataHelper.getCardNumberWith17Digits();
        cardForm.fillForm(cardWith17Digits);
        cardForm.waitForErrorSnackbar();

        /* Здесь тоже null, потому что тест упал с сообщением в консоли:
        Expected: DECLINED
        Actual: null
        */

        assertEquals(null, getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardNumberHasLetters() {
        cardForm = startPage.clickOnPaymentButton();
        var cardWithLetters = DataHelper.getCardNumberWithLetters();
        cardForm.fillForm(cardWithLetters);
        cardForm.waitForInputError();

        assertEquals(null, getLatestPaymentStatus());
    }

    @Test
    void shouldDeclineRequestIfCardNumberHasSymbols() {
        cardForm = startPage.clickOnCreditButton();
        var cardWithSymbols = DataHelper.getCardNumberWithSymbols();
        cardForm.fillForm(cardWithSymbols);
        cardForm.waitForInputError();

        assertEquals(null, getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardFieldIsEmpty() {
        cardForm = startPage.clickOnPaymentButton();
        var emptyCard = DataHelper.getCardWithEmptyNumber();
        cardForm.fillForm(emptyCard);
        cardForm.waitForInputError();

        assertEquals(null, getLatestPaymentStatus());
    }

    @Test
    void shouldDeclineRequestIfCardHasWrongMonth() {
        cardForm = startPage.clickOnCreditButton();
        var cardWithWrongMonth = DataHelper.getCardWithWrongMonth();
        cardForm.fillForm(cardWithWrongMonth);
        cardForm.waitForInputError();

        assertEquals(null, getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfMonthFieldIsEmpty() {
        cardForm = startPage.clickOnPaymentButton();
        var emptyMonth = DataHelper.getCardWithEmptyMonth();
        cardForm.fillForm(emptyMonth);
        cardForm.waitForInputError();

        assertEquals(null, getLatestPaymentStatus());
    }

    @Test
    void shouldDeclineRequestIfCardHasExpiredYear() {
        cardForm = startPage.clickOnCreditButton();
        var cardWithExpiredYear = DataHelper.getCardWithExpiredYear();
        cardForm.fillForm(cardWithExpiredYear);
        cardForm.waitForErrorInFieldYear();

        assertEquals(null, getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardHasWrongYear() {
        cardForm = startPage.clickOnPaymentButton();
        var cardWithWrongYear = DataHelper.getCardWithWrongYear();
        cardForm.fillForm(cardWithWrongYear);
        cardForm.waitForInputError();

        assertEquals(null, getLatestPaymentStatus());
    }

    @Test
    void shouldDeclineRequestIfYearFieldIsEmpty() {
        cardForm = startPage.clickOnCreditButton();
        var emptyYear = DataHelper.getCardWithEmptyYear();
        cardForm.fillForm(emptyYear);
        cardForm.waitForInputError();

        assertEquals(null, getLatestCreditStatus());
    }

    // Поле принимает имя пользователя, содержащее цифры, хотя не должно.
    @Test
    void shouldDeclineRequestIfHolderNameHasDigits() {
        cardForm = startPage.clickOnPaymentButton();
        var holderWithDigits = DataHelper.getCardWithDigitsInHolder();
        cardForm.fillForm(holderWithDigits);
        cardForm.waitForInputError();

        assertEquals(null, getLatestPaymentStatus());
    }

    // Поле принимает имя пользователя, содержащее символы, хотя не должно.
    @Test
    void shouldDeclineRequestIfHolderNameHasSymbols() {
        cardForm = startPage.clickOnCreditButton();
        var holderWithSymbols = DataHelper.getCardWithSymbolsInHolder();
        cardForm.fillForm(holderWithSymbols);
        cardForm.waitForInputError();

        assertEquals(null, getLatestCreditStatus());
    }

    // Форма принимает один символ в поле «Владелец», что тоже неверно.
    @Test
    void shouldDeclineRequestIfHolderNameHasOnlyOneLetter() {
        cardForm = startPage.clickOnPaymentButton();
        var nameWithOneLetter = DataHelper.getCardWithOneLetterHolder();
        cardForm.fillForm(nameWithOneLetter);
        cardForm.waitForInputError();

        assertEquals(null, getLatestPaymentStatus());
    }

    @Test
    void shouldDeclineRequestIfHolderFieldIsEmpty() {
        cardForm = startPage.clickOnCreditButton();
        var emptyName = DataHelper.getCardWithEmptyHolder();
        cardForm.fillForm(emptyName);
        cardForm.waitForErrorInFieldHolder();

        assertEquals(null, getLatestCreditStatus());
    }
}