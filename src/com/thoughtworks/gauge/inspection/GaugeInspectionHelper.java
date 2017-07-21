package com.thoughtworks.gauge.inspection;

import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

class GaugeInspectionHelper {
    @NotNull
    static List<GaugeError> getErrors(File directory) {
        try {
            GaugeSettingsModel settings = getGaugeSettings();
            ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), "--validate");
            GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
            processBuilder.directory(directory);
            Process process = processBuilder.start();
            process.waitFor();
            String[] errors = GaugeUtil.getOutput(process.getInputStream(), "\n").split("\n");
            return Arrays.stream(errors).map(GaugeError::getInstance).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException | InterruptedException | GaugeNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
