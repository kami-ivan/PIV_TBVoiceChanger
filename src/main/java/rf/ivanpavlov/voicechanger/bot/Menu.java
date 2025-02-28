package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Menu {

    public String getTextMainMenu(Map<String, String> settings) {
        return "Главное меню:\n\n" +
                "Чтобы настроить параметры - нажмите на соответствующий параметр " +
                "и следуйте дальнейшим указаниям!\n\n" +
                "❗Ваши параметры:❗\n" +
                "Модель: " + settings.get("model") + "\n" +
                "Питч: " + settings.get("pitch") + "\n" +
                "Качество: " + settings.get("algorithm") + "\n\n" +
                "Если вас все устраивает - нажимайте на \"готово\" и загружайте аудиофайл!";
    }

    public InlineKeyboardMarkup getMarkupMainMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        // Первая кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("модели")
                .callbackData("menu_models")
                .build());

        // Вторая кнопка
        row2.add(InlineKeyboardButton.builder()
                .text("питч")
                .callbackData("menu_pitch")
                .build());

        // Вторая кнопка
        row2.add(InlineKeyboardButton.builder()
                .text("качество")
                .callbackData("menu_algorithm")
                .build());


        rows.add(row1);
        rows.add(row2);
        markup.setKeyboard(rows);
        return markup;
    }

    public String getTextMenuModels() {
        return "Выберите голосовую модель:";
    }

    public InlineKeyboardMarkup getMarkupMenuModels(String value) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();


        String str1 = "";
        String str2 = "";
        switch (value) {
            case "man":
                str1 = "✅";
                break;
            case "woman":
                str2 = "✅";
                break;
            case "":
                break;
        }

        // Первая кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("мужчина" + str1)
                .callbackData("model_man")
                .build());

        // Вторая кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("женщина" + str2)
                .callbackData("model_woman")
                .build());

        row2.add(InlineKeyboardButton.builder()
                .text("все модели")
                .callbackData("model_all")
                .build());

        // третья кнопка
        row3.add(InlineKeyboardButton.builder()
                .text("назад")
                .callbackData("main_menu")
                .build());

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        markup.setKeyboard(rows);

        return markup;
    }

    public String getTextMenuAllModels(List<String> models) {
        String text = "Вот список всех моделей: \n\n" +
                "Чтобы выбрать модель: \nнажмите на \"выбрать модель\" и отправьте название модели.\n\n";

        text += models.toString().replaceAll(", ", "\n");
        return text;
    }

    public InlineKeyboardMarkup getMarkupMenuAllModels() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("назад")
                .callbackData("menu_models")
                .build());

        row2.add(InlineKeyboardButton.builder()
                .text("выбрать модель")
                .callbackData("choosing_model")
                .build());


        rows.add(row1);
        rows.add(row2);
        markup.setKeyboard(rows);

        return markup;
    }

    public String getTextMenuPitch() {
        return "Выберите питч: \n" +
                "Pitch - высота тона вашего голоса. \n" +
                "Пример: +12 - повышение на октаву.";
    }

    public InlineKeyboardMarkup getMarkupMenuPitch(String value) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        String str1 = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        switch (value) {
            case "-12":
                str1 = "✅";
                break;
            case "0":
                str2 = "✅";
                break;
            case "12":
                str3 = "✅";
                break;
            case "personal":
                str4 = "✅";
                break;
            case "":
                break;
        }
        // Первая кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("-12" + str1)
                .callbackData("pitch-12")
                .build());
        // Вторая кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("0" + str2)
                .callbackData("pitch0")
                .build());
        // третья кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("+12" + str3)
                .callbackData("pitch+12")
                .build());
        // четвертая кнопка
        row2.add(InlineKeyboardButton.builder()
                .text("свое значение" + str4)
                .callbackData("pitch_personal")
                .build());
        row3.add(InlineKeyboardButton.builder()
                .text("назад")
                .callbackData("main_menu")
                .build());

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        markup.setKeyboard(rows);

        return markup;
    }

    public String getTextMenuAlgorithm() {
        return "Выберите качество:";
    }

    public InlineKeyboardMarkup getMarkupMenuAlgorithm(String value) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        String str1 = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        switch (value) {
            case "low":
                str1 = "✅";
                break;
            case "medium":
                str2 = "✅";
                break;
            case "high":
                str3 = "✅";
                break;
            case "super":
                str4 = "✅";
                break;
            case "":
                break;
        }
        // Первая кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("низкое" + str1)
                .callbackData("algorithm_low")
                .build());

        // Вторая кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("среднее" + str2)
                .callbackData("algorithm_medium")
                .build());

        // третья кнопка
        row1.add(InlineKeyboardButton.builder()
                .text("высокое" + str3)
                .callbackData("algorithm_high")
                .build());

        row2.add(InlineKeyboardButton.builder()
                .text("супер" + str4)
                .callbackData("algorithm_super")
                .build());

        // четвертая кнопка
        row3.add(InlineKeyboardButton.builder()
                .text("назад")
                .callbackData("main_menu")
                .build());

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        markup.setKeyboard(rows);

        return markup;
    }

}
