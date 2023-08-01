import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.lang.System.exit;

/*
Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном порядке, разделенные пробелом:
Фамилия Имя Отчество датарождения номертелефона пол

Форматы данных:
фамилия, имя, отчество - строки
датарождения - строка формата dd.mm.yyyy
номертелефона - целое беззнаковое число без форматирования
пол - символ латиницей f или m.

Полученную строку попытаться распарсить, бросить исключения, сохранить пользовательскую строку в файл с названием, идентичным
фамилии. Однофамильцев сохранять в один файл, разными строками.

 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Добрый день! Я запрошу у вас некоторую информацию.\n" +
                "Введите ниже одной строкой, разделяя пробелами: \n" +
                "фамилию, имя, отчество, дату рождения числами в формате день.месяц.год, номер телефона, пол(f или m).\n" +
                "!!!В дате рождения должны быть только числа, разделенные точками! Например, 01.01.2000\n" +
                "Номер телефона числом без плюса, пробелов, дефисов!!! Пол латиницей!!!");

        String userString = askData();
        String[] dataArray = userString.replaceAll("\\s{2,}", " ").split(" ");
        dataArray = basicCheckingUserInput(dataArray);
        System.out.println(Arrays.toString(dataArray));
        String path = null;
        System.out.println("Правильно ли мы поняли, что ваша фамилия " + dataArray[0] + "?\n" +
                "Нажмите 1, если да.\n" +
                "Нажмите 2, если нет и надо поискать среди других ваших данных фамилию.");
        Scanner iscanner = null;

        try {
            iscanner = new Scanner(System.in);
            System.out.print("Ваш выбор: ");
            int choice = iscanner.nextInt();
            switch (choice) {
                case 1:
                    path = dataArray[0];
                    break;
                case 2:
                    System.out.println("Ваша фамилия " + dataArray[1] + "? Нажмите 1, если да. Нажмите 2, если нет, мы поищем еще!");
                    int choice2 = iscanner.nextInt();
                    switch (choice2) {
                        case 1:
                            path = dataArray[1];
                            break;
                        case 2:
                            System.out.println("Ваша фамилия " + dataArray[2] + "? Нажмите 1, если да.\n" +
                                    "Нажмите 2, и вам придется повторить вход в программу с самого начала!");
                            int choice3 = iscanner.nextInt();
                            switch (choice3) {
                                case 1:
                                    path = dataArray[2];
                                    break;
                                case 2:
                                    System.out.println("Программа завершается, но мы очень ждем вас снова. Просим быть повнимательнее с введением данных!");
                                    iscanner.close();
                                    exit(0);
                            }

                    }

            }
        } catch (InputMismatchException e) {
            assert iscanner != null;
            iscanner.close();
            System.out.println("Программа завершается, но мы очень ждем вам снова. Просим быть повнимательнее с введением данных!");
            throw new InputMismatchChoice();
        }

        String stringForWriting = null;
        try {
            checkingSurnameNameMiddleName(dataArray);
            System.out.println("В файле, название которого будет совпадать с вашей фамилией, данные ФИО будут занесены в следующем порядке:\n" +
                    dataArray[0] + " " + dataArray[1] + " " + dataArray[2]+ ". Согласны ли вы с таким порядком хранения даннных ФИО в файле?\n" +
                    "Если согласны, нажмите 1. Если нет, нажмите 2, и вам придется запустить программу заново");

            int choice4 = iscanner.nextInt();
            switch (choice4){
                case 1:
                    stringForWriting = "<" + dataArray[0] + "><" + dataArray[1] + "><" + dataArray[2] + ">";
                    break;
                default:
                    System.out.println("Надеюсь, скоро увидимся снова. Будьте внимательнее при введении данных!");
                    iscanner.close();
                    exit(0);
            }
        } catch (CheckSurnameNameMiddleName e) {
            iscanner.close();
            throw new CheckSurnameNameMiddleName(dataArray[0], dataArray[1], dataArray[2]);
        } catch (InputMismatchException e) {
            iscanner.close();
            System.out.println("Программа завершается, но мы очень ждем вам снова. Просим быть повнимательнее с введением данных!");
            throw new InputMismatchChoice();
        }


        try {
            checkingDateOfBirth(dataArray);
            stringForWriting += "<" + dataArray[3] + ">";
        } catch (CheckPeriodsInDateOfBirth e) {
            iscanner.close();
            throw new CheckPeriodsInDateOfBirth();
        } catch (CheckDateOfBirth e) {
            iscanner.close();
            throw new CheckDateOfBirth((dataArray[3].split("\\."))[0], (dataArray[3].split("\\."))[1], (dataArray[3].split("\\."))[2]);
        } catch (CheckDateOfBirthThoroughly e) {
            iscanner.close();
            throw new CheckDateOfBirthThoroughly(parseInt(dataArray[3].split("\\.")[0]),
                    parseInt(dataArray[3].split("\\.")[1]),
                    parseInt(dataArray[3].split("\\.")[2]));
        }

        try {
            checkingPhoneNumber(dataArray);
            stringForWriting += "<" + dataArray[4] + ">";
        } catch (NumberFormatException e) {
            iscanner.close();
            throw new CheckPhoneNumber(dataArray[4]);
        }

        try {
            checkingGender(dataArray);
            stringForWriting += "<" + dataArray[5] + ">";
        } catch (RuntimeException e) {
            iscanner.close();
            throw new CheckGender(dataArray[5]);
        }

        try {
            fileDealing(path, stringForWriting);
        }catch(NullPointerException e){
            iscanner.close();
            exit(0);
        }finally {
            iscanner.close();
        }

    }


    public static String askData() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] data = input.replaceAll("\\s{2,}", " ").split(" ");
        if (data.length < 6) {
            System.out.println("Вы ввели меньше данных, чем требуется. Попытайтесь еще раз! Введите строку ниже!");
            askData();
        }
        if (data.length > 6) {
            System.out.println("Вы ввели больше данных, чем требуется. Попытайтесь еще раз! Введите строку ниже!");
            askData();
        }
//        scanner.close();
        return input;

    }

    public static void checkingSurnameNameMiddleName(String[] inputString) {
        if (!(inputString[0].matches("^[a-zA-Zа-яА-я]*$")) || !inputString[1].matches("^[a-zA-Zа-яА-я]*$") || !inputString[2].matches("^[a-zA-Zа-яА-я]*$")) {
            throw new CheckSurnameNameMiddleName(inputString[0], inputString[1], inputString[2]);
        }
    }

    public static void checkingDateOfBirth(String[] inputString) {
        if (inputString[3].charAt(2) != '.' || inputString[3].charAt(5) != '.') {
            throw new CheckPeriodsInDateOfBirth();
        }
        String[] splitDateOfBirth = inputString[3].split("\\.");
        if (!splitDateOfBirth[0].matches("\\d+") || !splitDateOfBirth[1].matches("\\d+") || !splitDateOfBirth[2].matches("\\d+")) {
            throw new CheckDateOfBirth(splitDateOfBirth[0], splitDateOfBirth[1], splitDateOfBirth[2]);
        }
        int day = parseInt(splitDateOfBirth[0]);
        int month = parseInt(splitDateOfBirth[1]);
        int year = parseInt(splitDateOfBirth[2]);
        if (day > 31 || month > 12 || year < 1900 || year > 2023) {
            throw new CheckDateOfBirthThoroughly(day, month, year);
        }
    }

    public static void checkingPhoneNumber(String[] inputString) {
        if (!inputString[4].matches("\\d+")) {
            throw new CheckPhoneNumber(inputString[4]);
        }
    }

    public static void checkingGender(String[] inputString) {
        if (!inputString[5].equals("m") && !inputString[5].equals("f")) {
            throw new CheckGender(inputString[5]);
        }
    }

    public static String[] basicCheckingUserInput(String[] inputString) {
        String[] correctedInputString = new String[6];
        for (int i = 0; i < correctedInputString.length; i++) {
            boolean flag = true;

            if (inputString[i].length() == 1 && inputString[i].matches("^[a-zA-Zа-яА-я]*$")) {
                correctedInputString[5] = inputString[i];
                flag = false;
            }

            if (flag && inputString[i].length() == 10 && inputString[i].contains(".")) {
                correctedInputString[3] = inputString[i];
                flag = false;
            }
            if (flag && inputString[i].matches("\\d+")) {
                correctedInputString[4] = inputString[i];
                flag = false;
            }
            if (flag && inputString[i].matches("^[a-zA-Zа-яА-я]*$") && inputString[i].length() != 1 && correctedInputString[0] == null && correctedInputString[1] == null && correctedInputString[2] == null) {
                correctedInputString[0] = inputString[i];
                flag = false;
            }
            if (flag && inputString[i].matches("^[a-zA-Zа-яА-я]*$") && inputString[i].length() != 1 && correctedInputString[1] == null
                    && correctedInputString[0] != null && correctedInputString[2] == null) {
                correctedInputString[1] = inputString[i];
                flag = false;
            }
            if (flag && inputString[i].matches("^[a-zA-Zа-яА-я]*$") && inputString[i].length() != 1 && correctedInputString[2] == null
                    && correctedInputString[0] != null && correctedInputString[1] != null) {
                correctedInputString[2] = inputString[i];
            }

        }
        for (String s : correctedInputString) {
            if (s == null) {
                throw new BasicCheckUserInput();
            }
        }

        return correctedInputString;
    }

    public static void fileDealing(String path, String data) throws NullPointerException {
        try {
            File file = new File(path);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(data + "\n");
            bw.flush();
            bw.close();
            System.out.println("Готово! Данные внесены в файл.");
        }  catch (NullPointerException | IOException e) {
            System.out.println("Осталось непонятным, где ваша фамилия. Войдите в программу заново и будьте более внимательны!");
        }
    }
}

class CheckSurnameNameMiddleName extends RuntimeException {
    public CheckSurnameNameMiddleName(String s1, String s2, String s3) {
        super("Неверный формат записи ФИО: " + s1 + " " + s2 + " " + s3);
    }
}

class CheckDateOfBirth extends RuntimeException {
    public CheckDateOfBirth(String day, String month, String year) {
        super("Неверный формат записи даты рождения: " + day + "." + month + "." + year);
    }
}

class CheckPeriodsInDateOfBirth extends RuntimeException {
    public CheckPeriodsInDateOfBirth() {
        super("Проверьте расстановку точек в дате рождения и в целом формат");
    }
}

class CheckDateOfBirthThoroughly extends RuntimeException {
    public CheckDateOfBirthThoroughly(int day, int month, int year) {
        super("В дате рождения представлены числа, которых не может быть: " + day + "." + month + "." + year);
    }
}

class CheckPhoneNumber extends NumberFormatException {
    public CheckPhoneNumber(String phoneNumber) {
        super("Номер телефона неверен: " + phoneNumber);
    }
}

class CheckGender extends RuntimeException {
    public CheckGender(String gender) {
        super("Проверьте правильность указания пола: " + gender);
    }
}

class BasicCheckUserInput extends NullPointerException {
    public BasicCheckUserInput() {
        super("Критическая ошибка при парсинге вашей строки. Проверьте правильность введенных данных еще раз!");
    }
}

class InputMismatchChoice extends InputMismatchException {
    public InputMismatchChoice() {
        super("Нет такого варианта выбора!");
    }
}