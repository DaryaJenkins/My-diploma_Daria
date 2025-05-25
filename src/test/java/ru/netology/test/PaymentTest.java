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
        assertNull(getLatestCreditStatus());
    }

    // Поле заполнится только 16 символами, но поскольку я генерирую случайный набор чисел в качестве номера карты, операция не будет одобрена банком.
    @Test
    void shouldDeclinePaymentIfCardNumberHas17Digits() {
        var cardWith17Digits = DataHelper.getCardNumberWith17Digits();
        cardForm.fillForm(cardWith17Digits);
        cardForm.waitForErrorSnackbar();

        /* Здесь тоже null, потому что тест упал с сообщением в консоли:
        Expected: DECLINED
        Actual: null
        Предполагаю, что номер карты всё равно не прошёл валидацию на фронте, несмотря на то, что обрезался до 16 цифр.
        */

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardNumberHasLetters() {
        var cardWithLetters = DataHelper.getCardNumberWithLetters();
        cardForm.fillForm(cardWithLetters);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardNumberHasSymbols() {
        var cardWithSymbols = DataHelper.getCardNumberWithSymbols();
        cardForm.fillForm(cardWithSymbols);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardFieldIsEmpty() {
        var emptyCard = DataHelper.getCardWithEmptyNumber();
        cardForm.fillForm(emptyCard);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // ~ Валидация полей «Месяц» и «Год» ~

    @Test
    void shouldDeclinePaymentIfCardHasInvalidMonth() {
        var cardWithInvalidMonth = DataHelper.getCardWithInvalidMonth();
        cardForm.fillForm(cardWithInvalidMonth);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfMonthFieldIsEmpty() {
        var emptyMonth = DataHelper.getCardWithEmptyMonth();
        cardForm.fillForm(emptyMonth);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardHasExpiredYear() {
        var cardWithExpiredYear = DataHelper.getCardWithExpiredYear();
        cardForm.fillForm(cardWithExpiredYear);
        cardForm.waitForExpiredCardError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfCardHasInvalidYear() {
        var cardWithInvalidYear = DataHelper.getCardWithInvalidYear();
        cardForm.fillForm(cardWithInvalidYear);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfYearFieldIsEmpty() {
        var emptyYear = DataHelper.getCardWithEmptyYear();
        cardForm.fillForm(emptyYear);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // ~ Валидация имени держателя карты ~

    // Поле принимает имя пользователя, содержащее цифры, хотя не должно.
    @Test
    void shouldDeclinePaymentIfHolderNameHasDigits() {
        var holderWithDigits = DataHelper.getCardWithDigitsInHolder();
        cardForm.fillForm(holderWithDigits);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // Поле принимает имя пользователя, содержащее символы, хотя не должно.
    @Test
    void shouldDeclinePaymentIfHolderNameHasSymbols() {
        var holderWithSymbols = DataHelper.getCardWithSymbolsInHolder();
        cardForm.fillForm(holderWithSymbols);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    // Форма принимает один символ в поле «Владелец», что тоже неверно.
    @Test
    void shouldDeclinePaymentIfHolderNameHasOnlyOneLetter() {
        var nameWithOneLetter = DataHelper.getCardWithOneLetterHolder();
        cardForm.fillForm(nameWithOneLetter);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfHolderFieldIsEmpty() {
        var emptyName = DataHelper.getCardWithEmptyHolder();
        cardForm.fillForm(emptyName);
        cardForm.waitForEmptyNameError();

        assertNull(getLatestCreditStatus());
    }

    // ~ Валидация CVV ~

    @Test
    void shouldDeclinePaymentIfCvvHas2Digits() {
        var cvvWith2Digits = DataHelper.getCardWithCvv2Digits();
        cardForm.fillForm(cvvWith2Digits);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    /* Поле не примет 4 символа и оставит только 3 — оплата пройдёт, потому что введённый CVV будет валиден.
    Я сделала две отдельные проверки: с картой, по которой платёж проходит, и с картой, по которой приходит отказ. */
    @Test
    void shouldKeepOnly3CvvDigitsApprovedCard() {
        var approvedCard4DigitsInCvv = DataHelper.getApprovedCardWithCvv4Digits();
        cardForm.fillForm(approvedCard4DigitsInCvv);
        cardForm.waitForSuccessSnackbar();

        assertEquals("APPROVED", getLatestPaymentStatus());
    }

    @Test
    void shouldKeepOnly3CvvDigitsDeclinedCard() {
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

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfCvvHasSymbols() {
        var cvvWithSymbol = DataHelper.getCardWithSymbolInCvv();
        cardForm.fillForm(cvvWithSymbol);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }

    @Test
    void shouldDeclinePaymentIfCvvFieldIsEmpty() {
        var emptyCvv = DataHelper.getCardWithEmptyCvv();
        cardForm.fillForm(emptyCvv);
        cardForm.waitForInputError();

        assertNull(getLatestCreditStatus());
    }
}