package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardForm {

    private final SelenideElement numberField = $("[placeholder='0000 0000 0000 0000']");
    private final SelenideElement monthField = $("[placeholder='08']");
    private final SelenideElement yearField = $("[placeholder='22']");
    private final SelenideElement holderField = $$("span.input__inner")
            .findBy(exactText("Владелец"))
            .$("input.input__control");
    private final SelenideElement cvvField = $("[placeholder='999']");
    private final SelenideElement continueButton = $$("button").findBy(exactText("Продолжить"));

    // Снекбары и ошибки заполнения полей
    private final SelenideElement successSnackbar = $(".notification.notification_status_ok");
    private final SelenideElement errorSnackbar = $(".notification.notification_status_error");
    private final SelenideElement inputError = $$("span.input__sub")
            .findBy(exactText("Неверный формат"));
    private final SelenideElement inputErrorExpiredCard = $$("span.input__sub")
            .findBy(exactText("Истёк срок действия карты"));
    private final SelenideElement inputErrorEmptyName = $$("span.input__sub")
            .findBy(exactText("Поле обязательно для заполнения"));

    public void fillForm(DataHelper.CardInfo cardInfo) {
        numberField.setValue(cardInfo.getNumber());
        monthField.setValue(cardInfo.getMonth());
        yearField.setValue(cardInfo.getYear());
        holderField.setValue(cardInfo.getHolder());
        cvvField.setValue(cardInfo.getCvv());
        continueButton.click();
    }

    public void waitForSuccessSnackbar() {
        successSnackbar.shouldBe(visible, Duration.ofSeconds(20))
                .shouldHave(text("Успешно"));
    }

    public void waitForErrorSnackbar() {
        errorSnackbar.shouldBe(visible, Duration.ofSeconds(20))
                .shouldHave(text("Ошибка"));
    }

    public void waitForInputError() {
        inputError.shouldBe(visible, Duration.ofSeconds(5));
    }

    public void waitForErrorInFieldYear() {
        inputErrorExpiredCard.shouldBe(visible, Duration.ofSeconds(5));
    }

    public void waitForErrorInFieldHolder() {
        inputErrorEmptyName.shouldBe(visible, Duration.ofSeconds(5));
    }
}