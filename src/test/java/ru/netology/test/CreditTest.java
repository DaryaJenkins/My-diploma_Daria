package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.pages.CardForm;
import ru.netology.pages.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.SQLHelper.*;

public class CreditTest {

    private StartPage startPage;
    private CardForm cardForm;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080");
        startPage = new StartPage();
        cardForm = startPage.clickOnCreditButton();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    void shouldSendRequestWithApprovedCard() {
        var approvedCard = DataHelper.getApprovedCard();
        cardForm.fillForm(approvedCard);
        cardForm.waitForSuccessSnackbar();

        assertEquals("APPROVED", getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestWithDeclinedCard() {
        var declinedCard = DataHelper.getDeclinedCard();
        cardForm.fillForm(declinedCard);
        cardForm.waitForErrorSnackbar();

        assertEquals("DECLINED", getLatestCreditStatus());
    }

    // ~ Валидация номера карты ~

    @Test
    void shouldDeclineRequestIfCardNumberHas15Digits() {
        var cardWith15Digits = DataHelper.getCardNumberWith15Digits();
        cardForm.fillForm(cardWith15Digits);
        cardForm.waitForInputError();

        // Статус null, потому что запрос не дойдёт до БД
        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardNumberHas17Digits() {
        var cardWith17Digits = DataHelper.getCardNumberWith17Digits();
        cardForm.fillForm(cardWith17Digits);
        cardForm.waitForErrorSnackbar();

        // Ожидаю, что запрос дойдёт до БД, и база данных вернёт отказ. Но запрос не доходит до БД.
        assertEquals("DECLINED", getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardNumberHasLetters() {
        var cardWithLetters = DataHelper.getCardNumberWithLetters();
        cardForm.fillForm(cardWithLetters);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardNumberHasSymbols() {
        var cardWithSymbols = DataHelper.getCardNumberWithSymbols();
        cardForm.fillForm(cardWithSymbols);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardFieldIsEmpty() {
        var emptyCard = DataHelper.getCardWithEmptyNumber();
        cardForm.fillForm(emptyCard);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // ~ Валидация полей «Месяц» и «Год» ~

    @Test
    void shouldDeclineRequestIfCardHasInvalidMonth() {
        var cardWithInvalidMonth = DataHelper.getCardWithInvalidMonth();
        cardForm.fillForm(cardWithInvalidMonth);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfMonthFieldIsEmpty() {
        var emptyMonth = DataHelper.getCardWithEmptyMonth();
        cardForm.fillForm(emptyMonth);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardHasExpiredYear() {
        var cardWithExpiredYear = DataHelper.getCardWithExpiredYear();
        cardForm.fillForm(cardWithExpiredYear);
        cardForm.waitForExpiredCardError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCardHasInvalidYear() {
        var cardWithInvalidYear = DataHelper.getCardWithInvalidYear();
        cardForm.fillForm(cardWithInvalidYear);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfYearFieldIsEmpty() {
        var emptyYear = DataHelper.getCardWithEmptyYear();
        cardForm.fillForm(emptyYear);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // ~ Валидация имени держателя карты ~

    // Поле принимает имя пользователя, содержащее цифры, хотя не должно.
    @Test
    void shouldDeclineRequestIfHolderNameHasDigits() {
        var holderWithDigits = DataHelper.getCardWithDigitsInHolder();
        cardForm.fillForm(holderWithDigits);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // Поле принимает имя пользователя, содержащее символы, хотя не должно.
    @Test
    void shouldDeclineRequestIfHolderNameHasSymbols() {
        var holderWithSymbols = DataHelper.getCardWithSymbolsInHolder();
        cardForm.fillForm(holderWithSymbols);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // Форма принимает один символ в поле «Владелец», что тоже неверно.
    @Test
    void shouldDeclineRequestIfHolderNameHasOnlyOneLetter() {
        var nameWithOneLetter = DataHelper.getCardWithOneLetterHolder();
        cardForm.fillForm(nameWithOneLetter);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfHolderFieldIsEmpty() {
        var emptyName = DataHelper.getCardWithEmptyHolder();
        cardForm.fillForm(emptyName);
        cardForm.waitForEmptyNameError();

        assertNull(getLatestCreditStatus());
    }

    // ~ Валидация CVV ~

    @Test
    void shouldDeclineRequestIfCvvHas2Digits() {
        var cvvWith2Digits = DataHelper.getCardWithCvv2Digits();
        cardForm.fillForm(cvvWith2Digits);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    /* При запуске автотеста и отправке запроса через Postman поле принимает значение из 4 символов,
    несмотря на то, что в Elements стоит ограничение в 3 символа,
    и возвращает 200 OK. */

    @Test
    void shouldDeclineRequestIfApprovedCardHasCvvWith4Digits() {
        var approvedCard4DigitsInCvv = DataHelper.getApprovedCardWithCvv4Digits();
        cardForm.fillForm(approvedCard4DigitsInCvv);
        cardForm.waitForErrorSnackbar();

        assertEquals("DECLINED", getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfDeclinedCardHasCvvWith4Digits() {
        var declinedCard4DigitsInCvv = DataHelper.getDeclinedWithCvv4Digits();
        cardForm.fillForm(declinedCard4DigitsInCvv);
        cardForm.waitForErrorSnackbar();

        assertEquals("DECLINED", getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCvvHasLetters() {
        var cvvWithLetters = DataHelper.getCardWithLetterInCvv();
        cardForm.fillForm(cvvWithLetters);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCvvHasSymbols() {
        var cvvWithSymbol = DataHelper.getCardWithSymbolInCvv();
        cardForm.fillForm(cvvWithSymbol);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclineRequestIfCvvFieldIsEmpty() {
        var emptyCvv = DataHelper.getCardWithEmptyCvv();
        cardForm.fillForm(emptyCvv);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }
}