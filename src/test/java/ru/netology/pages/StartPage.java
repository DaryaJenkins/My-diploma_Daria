package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class StartPage {

    private final SelenideElement paymentButton = $$(".button").findBy(text("Купить"));
    private final SelenideElement creditButton = $$(".button").findBy(text("Купить в кредит"));

    public CardForm clickOnPaymentButton() {
        paymentButton.shouldBe(visible).click();
        return new CardForm();
    }

    public CardForm clickOnCreditButton() {
        creditButton.shouldBe(visible).click();
        return new CardForm();
    }
}