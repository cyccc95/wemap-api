package com.wemap.api.common.util;

import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class ValidationUtil {

    private static String LoginIdRegex = "^[a-z][a-z0-9]{5,19}$";
    private static String PasswordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*?])[A-Za-z\\d!@#$%^&*?]{9,16}$";
    private static String NameRegex = "^[가-힣]{2,10}$";
    private static String EmailRegex = "^[a-z][a-z0-9]{5,19}$";
    private static String EmailCodeRegex = "^[a-zA-Z0-9]{8}";

    public static boolean isNullOrBlank(String string) {
        if (Objects.isNull(string) || string.isBlank() || string.equals("null")) {
            return true;
        }
        return false;
    }

    public static boolean checkLoginId(String loginId) {
        if (isNullOrBlank(loginId)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(LoginIdRegex);
            Matcher matcher = pattern.matcher(loginId);
            return matcher.matches();
        }
    }

    public static boolean checkPassword(String password) {
        if (isNullOrBlank(password)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(PasswordRegex);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        }
    }

    public static boolean checkName(String name) {
        if (isNullOrBlank(name)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(NameRegex);
            Matcher matcher = pattern.matcher(name);
            return matcher.matches();
        }
    }

    public static boolean checkBirthday(String birthday) {
        if (isNullOrBlank(birthday)|| birthday.length() != 8) {
            return false;
        } else {
            int year = Integer.parseInt(birthday.substring(0, 4));
            int month = Integer.parseInt(birthday.substring(4, 6));
            int date = Integer.parseInt(birthday.substring(6, 8));

            Calendar today = Calendar.getInstance();
            int currentYear = today.get(Calendar.YEAR);
            int currentMonth = today.get(Calendar.MONTH) + 1;
            int currentDate = today.get(Calendar.DAY_OF_MONTH);

            if (year < 1900 || year > currentYear) {
                return false;
            } else if (month < 1 || month > 12) {
                return false;
            } else if (date < 1 || date > 31) {
                return false;
            } else if ((month == 4 || month == 6 || month == 9 || month == 11) && date == 31) {
                return false;
            } else if (month == 2) {
                boolean isLeapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
                if (date > 29 || (date == 29 && !isLeapYear)) {
                    return false;
                } else {
                    // 생년월일이 현재 날짜보다 이전인지를 체크
                    return year < currentYear || (year == currentYear && month < currentMonth)
                            || (year == currentYear && month == currentMonth && date <= currentDate);
                }
            } else {
                // 생년월일이 현재 날짜보다 이전인지를 체크
                return year < currentYear || (year == currentYear && month < currentMonth)
                        || (year == currentYear && month == currentMonth && date <= currentDate);
            }
        }
    }

    public static boolean checkEmail(String email) {
        if (isNullOrBlank(email)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(EmailRegex);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }

    public static boolean checkEmailCode(String emailCode) {
        if (isNullOrBlank(emailCode)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(EmailCodeRegex);
            Matcher matcher = pattern.matcher(emailCode);
            return matcher.matches();
        }
    }
}
