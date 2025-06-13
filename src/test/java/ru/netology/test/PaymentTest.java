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

public class PaymentTest {

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
        cardForm = startPage.clickOnPaymentButton();
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
    void shouldApprovePaymentWithApprovedCard() {
        var approvedCard = DataHelper.getApprovedCard();
        cardForm.fillForm(approvedCard);
        cardForm.waitForSuccessSnackbar();

        assertEquals("APPROVED", getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentWithDeclinedCard() {
        var declinedCard = DataHelper.getDeclinedCard();
        cardForm.fillForm(declinedCard);
        cardForm.waitForErrorSnackbar();

        assertEquals("DECLINED", getLatestPaymentStatus());
    }

    // ~ Валидация номера карты ~

    @Test
    void shouldDeclinePaymentIfCardNumberHas15Digits() {
        var cardWith15Digits = DataHelper.getCardNumberWith15Digits();
        cardForm.fillForm(cardWith15Digits);
        cardForm.waitForInputError();

        // Статус null, потому что запрос не дойдёт до БД
        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardNumberHas17Digits() {
        var cardWith17Digits = DataHelper.getCardNumberWith17Digits();
        cardForm.fillForm(cardWith17Digits);
        cardForm.waitForErrorSnackbar();

        // Ожидаю, что запрос дойдёт до БД, и база данных вернёт отказ. Но запрос не доходит до БД.
        assertEquals("DECLINED", getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardNumberHasLetters() {
        var cardWithLetters = DataHelper.getCardNumberWithLetters();
        cardForm.fillForm(cardWithLetters);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardNumberHasSymbols() {
        var cardWithSymbols = DataHelper.getCardNumberWithSymbols();
        cardForm.fillForm(cardWithSymbols);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardFieldIsEmpty() {
        var emptyCard = DataHelper.getCardWithEmptyNumber();
        cardForm.fillForm(emptyCard);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    // ~ Валидация полей «Месяц» и «Год» ~

    @Test
    void shouldDeclinePaymentIfCardHasInvalidMonth() {
        var cardWithInvalidMonth = DataHelper.getCardWithInvalidMonth();
        cardForm.fillForm(cardWithInvalidMonth);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfMonthFieldIsEmpty() {
        var emptyMonth = DataHelper.getCardWithEmptyMonth();
        cardForm.fillForm(emptyMonth);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardHasExpiredYear() {
        var cardWithExpiredYear = DataHelper.getCardWithExpiredYear();
        cardForm.fillForm(cardWithExpiredYear);
        cardForm.waitForExpiredCardError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardHasInvalidYear() {
        var cardWithInvalidYear = DataHelper.getCardWithInvalidYear();
        cardForm.fillForm(cardWithInvalidYear);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfYearFieldIsEmpty() {
        var emptyYear = DataHelper.getCardWithEmptyYear();
        cardForm.fillForm(emptyYear);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    // ~ Валидация имени держателя карты ~

    // Поле принимает имя пользователя, содержащее цифры, хотя не должно.
    @Test
    void shouldDeclinePaymentIfHolderNameHasDigits() {
        var holderWithDigits = DataHelper.getCardWithDigitsInHolder();
        cardForm.fillForm(holderWithDigits);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    // Поле принимает имя пользователя, содержащее символы, хотя не должно.
    @Test
    void shouldDeclinePaymentIfHolderNameHasSymbols() {
        var holderWithSymbols = DataHelper.getCardWithSymbolsInHolder();
        cardForm.fillForm(holderWithSymbols);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    // Форма принимает один символ в поле «Владелец», что тоже неверно.
    @Test
    void shouldDeclinePaymentIfHolderNameHasOnlyOneLetter() {
        var nameWithOneLetter = DataHelper.getCardWithOneLetterHolder();
        cardForm.fillForm(nameWithOneLetter);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfHolderFieldIsEmpty() {
        var emptyName = DataHelper.getCardWithEmptyHolder();
        cardForm.fillForm(emptyName);
        cardForm.waitForEmptyNameError();

        assertNull(getLatestPaymentStatus());
    }

    // ~ Валидация CVV ~

    @Test
    void shouldDeclinePaymentIfCvvHas2Digits() {
        var cvvWith2Digits = DataHelper.getCardWithCvv2Digits();
        cardForm.fillForm(cvvWith2Digits);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    /* При запуске автотеста и отправке запроса через Postman поле принимает значение из 4 символов,
    несмотря на то, что в Elements стоит ограничение в 3 символа,
    и возвращает 200 OK. */

    @Test
    void shouldDeclinePaymentIfApprovedCardHasCvvWith4Digits() {
        var approvedCard4DigitsInCvv = DataHelper.getApprovedCardWithCvv4Digits();
        cardForm.fillForm(approvedCard4DigitsInCvv);
        cardForm.waitForErrorSnackbar();

        assertEquals("DECLINED", getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfDeclinedCardHasCvvWith4Digits() {
        var declinedCard4DigitsInCvv = DataHelper.getDeclinedWithCvv4Digits();
        cardForm.fillForm(declinedCard4DigitsInCvv);
        cardForm.waitForErrorSnackbar();

        assertEquals("DECLINED", getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCvvHasLetters() {
        var cvvWithLetters = DataHelper.getCardWithLetterInCvv();
        cardForm.fillForm(cvvWithLetters);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCvvHasSymbols() {
        var cvvWithSymbol = DataHelper.getCardWithSymbolInCvv();
        cardForm.fillForm(cvvWithSymbol);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }

    @Test
    void shouldDeclinePaymentIfCvvFieldIsEmpty() {
        var emptyCvv = DataHelper.getCardWithEmptyCvv();
        cardForm.fillForm(emptyCvv);
        cardForm.waitForInputError();

        assertNull(getLatestPaymentStatus());
    }
}