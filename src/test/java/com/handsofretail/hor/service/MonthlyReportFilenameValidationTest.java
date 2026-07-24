package com.handsofretail.hor.service;

import com.handsofretail.hor.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthlyReportFilenameValidationTest {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("^monthly_(\\d{1,2})_(\\d{4})\\.(xlsx|xls)$");

    // Mirrors the private static method in MonthlyReportServiceImpl
    private void validateFilename(String filename, Integer reportMonth, Integer reportYear) {
        if (filename == null) {
            throw new BadRequestException("Uploaded file name is missing");
        }

        Matcher matcher = FILENAME_PATTERN.matcher(filename);
        if (!matcher.matches()) {
            throw new BadRequestException(
                    "Uploaded file name does not match report month and year. Expected: monthly_"
                            + reportMonth + "_" + reportYear + ".xlsx or .xls");
        }

        int fileMonth = Integer.parseInt(matcher.group(1));
        int fileYear = Integer.parseInt(matcher.group(2));

        if (fileMonth != reportMonth || fileYear != reportYear) {
            throw new BadRequestException(
                    "Uploaded file name does not match report month and year. Expected: monthly_"
                            + reportMonth + "_" + reportYear + ".xlsx or .xls");
        }
    }

    @ParameterizedTest(name = "valid: {0} month={1} year={2}")
    @CsvSource({
            "monthly_8_2026.xlsx, 8, 2026",
            "monthly_8_2026.xls,  8, 2026",
            "monthly_12_2025.xlsx, 12, 2025",
            "monthly_12_2025.xls,  12, 2025",
            "monthly_1_2024.xlsx, 1, 2024",
            "monthly_1_2024.xls,  1, 2024"
    })
    void validFilenamesShouldPass(String filename, int month, int year) {
        assertThatNoException().isThrownBy(() -> validateFilename(filename.trim(), month, year));
    }

    @ParameterizedTest(name = "pattern invalid: {0}")
    @CsvSource({
            "sales_aug_2026.xlsx, 8, 2026",
            "monthly_aug_2026.xlsx, 8, 2026",
            "monthly_8_2026.csv, 8, 2026",
            "monthly_8_2026.pdf, 8, 2026",
            "MONTHLY_8_2026.xlsx, 8, 2026",
            "monthly_8_26.xlsx, 8, 2026",
            "monthly__2026.xlsx, 8, 2026"
    })
    void invalidPatternShouldThrow(String filename, int month, int year) {
        assertThatThrownBy(() -> validateFilename(filename.trim(), month, year))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "Expected: monthly_" + month + "_" + year + ".xlsx or .xls");
    }

    @Test
    void monthMismatchShouldThrow() {
        assertThatThrownBy(() -> validateFilename("monthly_7_2026.xls", 8, 2026))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Expected: monthly_8_2026.xlsx or .xls");
    }

    @Test
    void yearMismatchShouldThrow() {
        assertThatThrownBy(() -> validateFilename("monthly_8_2025.xls", 8, 2026))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Expected: monthly_8_2026.xlsx or .xls");
    }

    @Test
    void nullFilenameShouldThrow() {
        assertThatThrownBy(() -> validateFilename(null, 8, 2026))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Uploaded file name is missing");
    }
}